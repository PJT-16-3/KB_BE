package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.ProbabilityDTO;
import org.scoula.dto.PythonAptPredictRequestDTO;
import org.scoula.dto.PredictResponseDTO;
import org.scoula.mapper.AptMapper;
import org.scoula.mapper.ProbabilityMapper;
import org.scoula.mapper.UserMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Log4j2
public class PythonApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PYTHON_API_URL = "http://13.209.161.22/predict";
    private final String PYTHON_OFFICETEL_URL= "http://13.209.161.22/predict/officetel";
    private final ProbabilityMapper probabilityMapper ;
    private final UserMapper userMapper;
    private final AptMapper aptMapper;

    public PythonAptPredictRequestDTO buildPythonAptPredictRequestDTO(String pblancNo) {
        try {
            PythonAptPredictRequestDTO dto = probabilityMapper.selectPythonAptInfoByPblancNo(pblancNo);

            if (dto == null) {
                log.warn("No result found for pblancNo={}", pblancNo);
                return null; // 또는 Optional.empty() 형태로 바꾸기 가능
            }


            return dto;

        } catch (Exception e) {
            log.error("Error building PythonAptPredictRequestDTO for pblancNo={}", pblancNo, e);
            throw new RuntimeException("Failed to build PythonAptPredictRequestDTO", e);
        }
    }


    public double requestPrediction(PythonAptPredictRequestDTO input) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PythonAptPredictRequestDTO> request = new HttpEntity<>(input, headers);

        ResponseEntity<PredictResponseDTO> response = restTemplate.postForEntity(
                PYTHON_API_URL,
                request,
                PredictResponseDTO.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getWinProbability();
        } else {
            throw new RuntimeException("FastAPI 예측 요청 실패: " + response.getStatusCode());
        }
    }




}