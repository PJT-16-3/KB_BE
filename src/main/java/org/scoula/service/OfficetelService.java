package org.scoula.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.*;
import org.scoula.mapper.AptMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
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

public class OfficetelService {
    @Value("${house.decoding.key}")
    private String API_KEY;
    @Value("${office.url}")
    private String OFFICETEL_URL;
    @Value("${office.type.url}")
    private String OFFICETEL_TYPE_URL;

    private final AptMapper officeMapper;

    public OfficetelResponseDTO fetchOfficetelData(int page, int perPage) {
        try {
            // 오늘 날짜 월로 쿼리 날리기 yyyy-MM 포맷
//            String startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String startDate = "2025-07";

            StringBuilder urlBuilder = new StringBuilder(OFFICETEL_URL);
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
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.readValue(sb.toString(), OfficetelResponseDTO.class);

        } catch (Exception e) {
            log.error("오피스텔 데이터 요청 중 오류 발생", e);
            return null;
        }
    }

    public void saveOfficetelData(OfficetelResponseDTO responseDto) {
        if (responseDto != null && responseDto.getData() != null) {
            for (OfficetelDTO dto : responseDto.getData()) {
                if (dto.getMvnPrearngeYm() != null) {
                    LocalDate converted = LocalDate.parse(dto.getMvnPrearngeYm() + "01", DateTimeFormatter.ofPattern("yyyyMMdd"));
                    dto.setMvnPrearngeYm(converted.toString()); // "2025-08-01" 형식
                }

                officeMapper.insertOfficetel(dto);
            }
        }
    }

    public OfficetelTypeResponseDTO fetchOfficetelTypeData(String houseManageNo) {
        try {
            StringBuilder urlBuilder = new StringBuilder(OFFICETEL_TYPE_URL);
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
            return mapper.readValue(sb.toString(), OfficetelTypeResponseDTO.class);

        } catch (Exception e) {
            log.error("아파트 타입 데이터 요청 중 오류 발생", e);
            return null;
        }
    }

    public List<OfficetelIdxDTO> getOfficetelIdxAndHouseManageNo() {
        List<OfficetelIdxDTO> aptList = officeMapper.getIdxAndHouseManageNoFromOfficetel();
        return aptList;
    }

    public void saveOfficetelTypes() {
        List<OfficetelIdxDTO> officetelList = getOfficetelIdxAndHouseManageNo();

        for (OfficetelIdxDTO dto : officetelList) {
            String houseManageNo = dto.getHouseManageNo();
            Integer officetelIdx = dto.getOfficetelIdx();

            // api 호출
            OfficetelTypeResponseDTO responseDTO = fetchOfficetelTypeData(houseManageNo);
            if (responseDTO == null || responseDTO.getData() == null) continue;

            // 2. 응답 결과 순회하며 apt_type 테이블 저장
            for (OfficetelTypeDTO typeDTO : responseDTO.getData()) {
                typeDTO.setOfficetelIdx(officetelIdx); // foreign key 설정
                officeMapper.insertOfficetelType(typeDTO);
            }
        }
    }

    public void syncOfficetelData() {
        try {
            OfficetelResponseDTO response = fetchOfficetelData(1, 1);
            if (response == null) {
                log.warn("[syncOfficetelData] 오피스텔 데이터 응답이 null입니다. 동기화 중단.");
                return;
            }

            Integer matchCount = response.getMatchCount();
            log.info("[syncOfficetelData] 총 Match 수: {}", matchCount);

            if (matchCount > 1) {
                response = fetchOfficetelData(1, matchCount);
                if (response == null) {
                    log.warn("[syncOfficetelData] 전체 데이터 재요청 실패. 동기화 중단.");
                    return;
                }
            }

            // 저장 처리
            saveOfficetelData(response);
            log.info("[syncOfficetelData] 오피스텔 본 데이터 저장 완료");

            if (response.getData() != null && !response.getData().isEmpty()) {
                for (OfficetelDTO officetel : response.getData()) {
                    log.info("저장된 Officetel: {}", officetel);
                }
            } else {
                log.warn("[syncOfficetelData] 데이터 리스트가 비어 있습니다.");
            }


            try {
                saveOfficetelTypes();
                log.info("[syncOfficetelData] 오피스텔 타입 데이터 저장 완료");
            } catch (Exception e) {
                log.error("[syncOfficetelData] 오피스텔 타입 저장 중 오류 발생", e);
            }

        } catch (Exception e) {
            log.error("[syncOfficetelData] 전체 동기화 중 예외 발생", e);
        }
    }

    public void deleteOldOfficetelDataBeforeThisMonth() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        log.info("오피스텔 데이터 삭제 : {}", firstDayOfMonth);
        officeMapper.deleteOldFromOfficetel(firstDayOfMonth);
    }

    public OfficetelDetailDTO getOfficetelDetail(String pblancNo) {
        try {

            OfficetelDetailDTO dto = officeMapper.getOfficetelDetails(pblancNo);
            int aptIdx = officeMapper.findOfficetelIdxByPblancNo(pblancNo);
            if (dto == null || aptIdx == 0 ) {
                log.warn("해당 공고번호 [{}]에 대한 청약 정보가 존재하지 않습니다.", pblancNo);
                throw new IllegalArgumentException("해당 공고에 대한 정보가 존재하지 않습니다.");
            }
            List<InfraPlaceDTO> infraPlaces = officeMapper.getOfficetelInfraPlace(aptIdx);
            dto.setInfraPlaces(infraPlaces);
            return dto;
        } catch (Exception e) {
            log.error("청약 공고 상세 조회 중 예외 발생. pblancNo: {}, error: {}", pblancNo, e.getMessage(), e);
            throw new IllegalStateException("청약 공고 상세 정보를 조회하는 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public void incrementOfficeViewCount(String pblancNo) {
        int updated = officeMapper.incrementOfficetelViewCount(pblancNo);
        if (updated == 0 ) {
            throw new IllegalArgumentException("존재하지 않는 공고번호 입니다."+pblancNo);
        }
    }


}
