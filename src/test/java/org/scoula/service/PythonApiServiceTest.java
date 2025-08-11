package org.scoula.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.RootConfig;
import org.scoula.dto.PythonAptPredictRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PythonApiService.class, RootConfig.class })
@Log4j2
class PythonApiServiceTest {

    @Autowired
    private PythonApiService pythonApiService;

    @Test
    void buildPythonAptPredictRequestDTO() {
        PythonAptPredictRequestDTO pythonAptPredictRequestDTO = pythonApiService.buildPythonAptPredictRequestDTO("2025000266");
        log.info(pythonAptPredictRequestDTO);
    }

    @Test
    void requestPrediction() {
    }
}