package org.scoula.service;

import lombok.extern.log4j.Log4j2;
import org.scoula.dto.AptDTO;
import org.scoula.dto.OfficetelDTO;
import org.scoula.dto.PlaceDTO;
import org.scoula.mapper.AptMapper;
import org.scoula.mapper.PlaceMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
@Log4j2
public class KakaoMapInfraService {

    private static final String CATEGORY_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/category.json";

    private final AptMapper aptMapper;
    private final PlaceMapper placeMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.map.rest_key}")
    private String kakaoRestKey;

    public KakaoMapInfraService(AptMapper aptMapper, PlaceMapper placeMapper) {
        this.aptMapper = aptMapper;
        this.placeMapper = placeMapper;
    }

    public void fetchAndSavePlacesForAll() {
        List<AptDTO> aptList = aptMapper.findAllAptLocations();
        List<OfficetelDTO> officetelList = aptMapper.findAllOfficetelLocations();

        Map<String, String> categoryMap = Map.of(
                "HP8", "hospital",
                "MT1", "mart",
                "SC4", "school",
                "SW8", "subway",
                "PS3", "kindergarten"
        );

        for (AptDTO apt : aptList) {
            processLocation(apt.getAptIdx(), null, apt.getLatitude(), apt.getLongitude(), categoryMap);
        }

        for (OfficetelDTO officetel : officetelList) {
            processLocation(null, officetel.getOfficetelIdx(), officetel.getLatitude(), officetel.getLongitude(), categoryMap);
        }
    }

    private void processLocation(Integer aptIdx, Integer officetelIdx,
                                 double latitude, double longitude,
                                 Map<String, String> categoryMap) {

        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            String categoryCode = entry.getKey();
            String placeType = entry.getValue();

            int radius;
            switch (placeType) {
                case "hospital": radius = 4000; break;
                case "mart": radius = 4000; break;
                case "school": radius = 500; break;
                case "subway": radius = 1000; break;
                case "kindergarten": radius = 500; break;
                default: radius = 1000;
            }

            List<PlaceDTO> places = searchByCategory(latitude, longitude, categoryCode, radius, placeType);

            if (places == null || places.isEmpty()) {
                log.info("검색 결과 없음: {} / {}", placeType, aptIdx != null ? "aptIdx=" + aptIdx : "officetelIdx=" + officetelIdx);
                continue;
            }

            for (PlaceDTO place : places) {
                place.setAptIdx(aptIdx);
                place.setOfficetelIdx(officetelIdx);
                place.setPlaceType(placeType);

                // ✅ 중복 체크 없이 항상 UPSERT
                switch (placeType) {
                    case "hospital":     placeMapper.insertHospital(place); break;
                    case "school":       placeMapper.insertSchool(place); break;
                    case "mart":         placeMapper.insertMart(place); break;
                    case "subway":       placeMapper.insertSubway(place); break;
                    case "kindergarten": placeMapper.insertKindergarten(place); break;
                }
                log.info("UPSERT 완료: {} {} {}", aptIdx != null ? "aptIdx=" + aptIdx : "officetelIdx=" + officetelIdx, placeType, place.getPlaceName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<PlaceDTO> searchByCategory(double latitude, double longitude,
                                            String categoryCode, int radius, String placeType) {
        int page = 1;
        int size = 15;
        List<PlaceDTO> allResults = new ArrayList<>();

        while (true) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(CATEGORY_SEARCH_URL)
                    .queryParam("category_group_code", categoryCode)
                    .queryParam("x", longitude)
                    .queryParam("y", latitude)
                    .queryParam("radius", radius)
                    .queryParam("size", size)
                    .queryParam("page", page);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoRestKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            if (body == null || body.get("documents") == null) break;

            List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
            if (documents.isEmpty()) break;

            for (Map<String, Object> doc : documents) {
                PlaceDTO dto = new PlaceDTO();
                dto.setPlaceName((String) doc.get("place_name"));

                String roadAddress = (String) doc.get("road_address_name");
                String jibunAddress = (String) doc.get("address_name");
                dto.setAddress(roadAddress != null && !roadAddress.isEmpty() ? roadAddress : jibunAddress);

                String distanceStr = (String) doc.get("distance");
                dto.setDistance(distanceStr != null && !distanceStr.isEmpty() ? Long.parseLong(distanceStr) : 0);

                dto.setLatitude(Double.parseDouble((String) doc.get("y")));
                dto.setLongitude(Double.parseDouble((String) doc.get("x")));
                dto.setCategoryName((String) doc.get("category_name"));

                allResults.add(dto);

                if (allResults.size() >= 5) {
                    return filterResults(allResults, placeType);
                }
            }

            Map<String, Object> meta = (Map<String, Object>) body.get("meta");
            if (meta != null && Boolean.TRUE.equals(meta.get("is_end"))) break;

            page++;
        }

        return filterResults(allResults, placeType);
    }

    private List<PlaceDTO> filterResults(List<PlaceDTO> allResults, String placeType) {
        switch (placeType) {
            case "hospital":
                return allResults.stream()
                        .filter(place -> isLargeHospital(place.getCategoryName()))
                        .sorted(Comparator.comparingLong(PlaceDTO::getDistance))
                        .limit(5)
                        .toList();

            case "mart":
                return allResults.stream()
                        .filter(place -> place.getCategoryName() != null
                                && place.getCategoryName().contains("대형마트"))
                        .sorted(Comparator.comparingLong(PlaceDTO::getDistance))
                        .limit(5)
                        .toList();

            default:
                return allResults.stream()
                        .sorted(Comparator.comparingLong(PlaceDTO::getDistance))
                        .limit(5)
                        .toList();
        }
    }

    private boolean isLargeHospital(String categoryName) {
        if (categoryName == null) return false;
        return categoryName.contains("종합병원")
                || categoryName.contains("대학병원")
                || categoryName.contains("시도립병원");
    }
}
