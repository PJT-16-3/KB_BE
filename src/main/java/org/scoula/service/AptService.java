package org.scoula.service;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.*;
import org.scoula.mapper.AptMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Log4j2
@PropertySource("classpath:/application.properties")
@RequiredArgsConstructor
public class AptService {

    @Value("${house.decoding.key}")
    private String API_KEY;
    @Value("${house.url}")
    private String HOUSE_URL;
    @Value("${house.type.url}")
    private String HOUSE_TYPE_URL;
    private final AptMapper aptMapper;

    public AptResponseDTO fetchAptData(int page, int perPage) {
        try {
            // 오늘 날짜 월로 쿼리 날리기 yyyy-MM 포맷
//            String startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String startDate = "2025-07";
            StringBuilder urlBuilder = new StringBuilder(HOUSE_URL);
            urlBuilder.append("?")
                    .append("page=").append(page)
                    .append("&perPage=").append(perPage)
                    .append("&cond[RCRIT_PBLANC_DE::GTE]=").append(startDate)
                    .append("&serviceKey=").append(URLEncoder.encode(API_KEY, "UTF-8"));

            URL url = new URL(urlBuilder.toString());

            // 헤더 설정
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            log.info("Response Code: {}", responseCode);

            BufferedReader br = (responseCode >= 200 && responseCode <= 299)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            conn.disconnect();
            log.info("응답 본문: {}", sb);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule()); // LocalDate 처리
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.readValue(sb.toString(), AptResponseDTO.class);

        } catch (Exception e) {
            log.error("아파트 데이터 요청 중 오류 발생", e);
            return null;
        }
    }

    public void saveAptData(AptResponseDTO responseDto) {
        if (responseDto != null && responseDto.getData() != null) {
            for (AptDTO dto : responseDto.getData()) {
                aptMapper.insertApt(dto);
            }
        }
    }

    public AptTypeResponseDTO fetchAptTypeData(String houseManageNo) {
        try {
            StringBuilder urlBuilder = new StringBuilder(HOUSE_TYPE_URL);
            urlBuilder.append("?page=1")
                    .append("&perPage=10000")
                    .append("&cond[HOUSE_MANAGE_NO::EQ]=").append(URLEncoder.encode(houseManageNo, "UTF-8"))
                    .append("&serviceKey=").append(URLEncoder.encode(API_KEY, "UTF-8"));

            URL url = new URL(urlBuilder.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            log.info("Response Code: {}", responseCode);

            BufferedReader br = (responseCode >= 200 && responseCode <= 299)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            conn.disconnect();
            log.info("응답 본문: {}", sb);

            ObjectMapper mapper = new ObjectMapper();
//            mapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_CAMEL_CASE);
            return mapper.readValue(sb.toString(), AptTypeResponseDTO.class);

        } catch (Exception e) {
            log.error("아파트 타입 데이터 요청 중 오류 발생", e);
            return null;
        }
    }

    //그..apt 테이블의 모든 idx 와 houseMangeNo을 가져옴 (api 호출하고 table 저장할려고)
    public List<AptIdxDTO> getAptIdxAndHouseManageNo() {
        List<AptIdxDTO> aptList = aptMapper.getIdxAndHouseMangeNo();
        return aptList;
    }

    public void saveAptTypes() {
        List<AptIdxDTO> aptList = getAptIdxAndHouseManageNo();

        for (AptIdxDTO dto : aptList) {
            String houseManageNo = dto.getHouseManageNo();
            Integer aptIdx = dto.getAptIdx();

            // api 호출
            AptTypeResponseDTO responseDTO = fetchAptTypeData(houseManageNo);
            if (responseDTO == null || responseDTO.getData() == null) continue;

            // 2. 응답 결과 순회하며 apt_type 테이블 저장
            for (AptTypeDTO typeDTO : responseDTO.getData()) {
                typeDTO.setAptIdx(aptIdx); // foreign key 설정
                aptMapper.insertAptType(typeDTO); // insert 수행
            }
        }
    }

    public void deleteOldAptDataBeforeThisMonth() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        log.info("아파트 데이터 삭제 : {}", firstDayOfMonth);
        aptMapper.deleteOld(firstDayOfMonth);
    }

    public void syncAptData() {

        AptResponseDTO response = fetchAptData(1, 1);
        Integer matchCount = response.getMatchCount();
        log.info("총 Match 수: {}", matchCount);

        if (matchCount > 1) {
            response = fetchAptData(1, matchCount);
        }

        saveAptData(response);

        if (response != null && response.getData() != null) {
            for (AptDTO apt : response.getData()) {
                log.info("저장된 APT: {}", apt);
            }
        } else {
            throw new IllegalStateException("response 또는 data가 null입니다.");
        }

        saveAptTypes();

    }

    public AptDetailDTO getAptDetail(String pblancNo) {
        try {
            AptDetailDTO dto = aptMapper.getAptDetails(pblancNo);

            if (dto == null || dto.getAptIdx() == null || dto.getAptIdx() == 0) {
                log.warn("해당 공고번호 [{}]에 대한 청약 정보가 존재하지 않습니다.", pblancNo);
                throw new IllegalArgumentException("해당 공고에 대한 정보가 존재하지 않습니다.");
            }

            // 이제 따로 쿼리 없이 바로 사용 가능
            List<InfraPlaceDTO> infraPlaces = aptMapper.getInfraPlace(dto.getAptIdx());
            dto.setInfraPlaces(infraPlaces);

            return dto;

        } catch (Exception e) {
            log.error("청약 공고 상세 조회 중 예외 발생. pblancNo: {}, error: {}", pblancNo, e.getMessage(), e);
            throw new IllegalStateException("청약 공고 상세 정보를 조회하는 중 오류가 발생했습니다.", e);
        }
    }


    @Transactional
    public void incrementAptViewCount(String pblancNo) {
        int updated = aptMapper.incrementAptViewCount(pblancNo);
        if (updated == 0) {
            throw new IllegalArgumentException("존재하지 않는 공고번호 입니다: " + pblancNo);
        }
    }

}
