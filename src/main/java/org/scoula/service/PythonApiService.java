package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.PredictResponseDTO;
import org.scoula.dto.PythonAptRequestDTO;
import org.scoula.dto.PythonOfficetelRequestDTO;
import org.scoula.mapper.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class PythonApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_APT_URL = "http://13.209.161.22/predict/apt";
    private final String PYTHON_OFFICETEL_URL = "http://13.209.161.22/predict/officetel";
    private final ProbabilityMapper probabilityMapper;
    private final UserMapper userMapper;
    private final AptMapper aptMapper;
    private final SelectedMapper selectedMapper;
    private final GaScoreMapper gaScoreMapper;

    public int checkResideCode(Integer usersIdx, String pblancNo) {
        try {
            // 입력 파라미터가 정확한지 확인하는 로그
            log.info("입력된 usersIdx: {}, pblancNo: {}", usersIdx, pblancNo);


            String userRegion = userMapper.findUserRegionByIdx(usersIdx);
            log.info("usersIdx {}의 사용자 지역: {}", usersIdx, userRegion);


            String aptRegion = aptMapper.findRegionByAptPblancNo(pblancNo);
            log.info("pblancNo {}의 아파트 지역: {}", pblancNo, aptRegion);


            log.info("비교 대상 -> 사용자 지역: '{}' vs 아파트 지역: '{}'", userRegion, aptRegion);

            if (userRegion != null && aptRegion != null && userRegion.startsWith(aptRegion)) {
                log.info("조건 충족: 사용자 지역이 아파트 지역으로 시작합니다. 1을 반환합니다.");
                return 1;
            } else {
                // 조건이 충족되지 않은 이유를 구체적으로 로그에 남깁니다.
                if (userRegion == null) {
                    log.warn("사용자 지역(userRegion)이 null입니다. 2를 반환합니다.");
                } else if (aptRegion == null) {
                    log.warn("아파트 지역(aptRegion)이 null입니다. 2를 반환합니다.");
                } else {
                    log.warn("사용자 지역('{}')이 아파트 지역('{}')으로 시작하지 않습니다. 2를 반환합니다.", userRegion, aptRegion);
                }
                return 2;
            }
        } catch (Exception e) {
            log.error("checkResideCode 오류 발생", e);
            return 0;
        }
    }


    public PythonAptRequestDTO buildPythonAptRequest(Integer usersIdx, String pblancNo) {
        PythonAptRequestDTO dto = null;
        try {
            dto = probabilityMapper.getPythonAptInfoByPblancNo(pblancNo);
            if (dto == null) {
                log.warn("해당 pblancNo에 대한 PythonAptInfo가 없습니다: {}", pblancNo);
                dto = new PythonAptRequestDTO(); // 비어있는 객체라도 생성
            }
            try {
                dto.setResideSecd(checkResideCode(usersIdx, pblancNo));
            } catch (Exception e) {
                log.error("거주 코드 설정 실패 (usersIdx: {}, pblancNo: {})", usersIdx, pblancNo, e);
                dto.setResideSecd(null);
            }

            try {
                dto.setHouseRank(selectedMapper.getUserRankByUsersIdx(usersIdx));
            } catch (Exception e) {
                log.error("주택 랭크 설정 실패 (usersIdx: {})", usersIdx, e);
                dto.setHouseRank(null);
            }
            try {
                dto.setScore(gaScoreMapper.getGaScoreByUserIdx(usersIdx));
            } catch (Exception e) {
                log.error("점수 설정 실패 (usersIdx: {})", usersIdx, e);
                dto.setScore(null);
            }

        } catch (Exception e) {
            log.error("buildPythonAptReqeust 메서드 처리 중 알 수 없는 오류 발생", e);
            if (dto == null) {
                dto = new PythonAptRequestDTO(); // 마지막 안전장치
            }
        }
        return dto;
    }


    public Map<String, Object> requestPrediction(PythonAptRequestDTO input) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PythonAptRequestDTO> request = new HttpEntity<>(input, headers);

        try {
            ResponseEntity<PredictResponseDTO> response = restTemplate.postForEntity(
                    PYTHON_APT_URL,
                    request,
                    PredictResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Map.of("probability", response.getBody().getProbability());
            } else {
                return Map.of("error", "FastAPI 예측 요청 실패", "status", response.getStatusCodeValue());
            }

        } catch (HttpClientErrorException e) {
            // FastAPI에서 보낸 4xx 응답 바디 추출
            String errorBody = e.getResponseBodyAsString();
            log.error("FastAPI 4xx 오류: {}", errorBody);

            // 예: {"detail":[{"loc":["body","house_rank"],"msg":"Input should be a valid integer"}]}
            return Map.of(
                    "error", "입력값을 다시 확인해 주세요.",
                    "detail", errorBody
            );

        } catch (HttpServerErrorException e) {
            log.error("FastAPI 5xx 오류: {}", e.getResponseBodyAsString());
            return Map.of("error", "FastAPI 서버 오류", "detail", e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("FastAPI 호출 중 알 수 없는 오류", e);
            return Map.of("error", "알 수 없는 오류가 발생했습니다.");
        }
    }

    public PythonOfficetelRequestDTO buildPythonOfficetelRequest(Integer usersIdx, String pblancNo) {
        PythonOfficetelRequestDTO info = probabilityMapper.getPythonOfficetelInfoByPblancNo(pblancNo);
        return info;
    }

    public Map<String, Object> requestOfficetelPrediction(PythonOfficetelRequestDTO input) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PythonOfficetelRequestDTO> request = new HttpEntity<>(input, headers);

        try {
            ResponseEntity<PredictResponseDTO> response = restTemplate.postForEntity(
                    PYTHON_OFFICETEL_URL,
                    request,
                    PredictResponseDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return Map.of("probability", response.getBody().getProbability());
            } else {
                return Map.of("error", "FastAPI 예측 요청 실패", "status", response.getStatusCodeValue());
            }

        } catch (HttpClientErrorException e) {
            // FastAPI에서 보낸 4xx 응답 바디 추출
            String errorBody = e.getResponseBodyAsString();
            log.error("FastAPI 4xx 오류: {}", errorBody);

            // 예: {"detail":[{"loc":["body","house_rank"],"msg":"Input should be a valid integer"}]}
            return Map.of(
                    "error", "입력값을 다시 확인해 주세요.",
                    "detail", errorBody
            );

        } catch (HttpServerErrorException e) {
            log.error("FastAPI 5xx 오류: {}", e.getResponseBodyAsString());
            return Map.of("error", "FastAPI 서버 오류", "detail", e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("FastAPI 호출 중 알 수 없는 오류", e);
            return Map.of("error", "알 수 없는 오류가 발생했습니다.");
        }
    }


}