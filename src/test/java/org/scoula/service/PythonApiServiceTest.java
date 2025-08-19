package org.scoula.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.RootConfig;
import org.scoula.dto.PythonAptRequestDTO;
import org.scoula.dto.PythonOfficetelRequestDTO;
import org.scoula.mapper.SelectedMapper;
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

    @Autowired
    private SelectedMapper selectedMapper;

    @Test
    @DisplayName("파이썬 요청 보내는 빌더")
    void buildPythonAptReqeust() {
        PythonAptRequestDTO dto = pythonApiService.buildPythonAptRequest(43,"2025000266");
        log.info(dto.toString());
    }


    @Test
    void checkResideCode() {
        int i = pythonApiService.checkResideCode(70, "2025000303");
        System.out.println("🔥🔥🔥🔥🔥🔥🔥🔥i = " + i);
    }

    @Test
    void buildPythonOfficetelRequest(){
        PythonOfficetelRequestDTO dto = pythonApiService.buildPythonOfficetelRequest(43, "2025950037");
        log.info(dto.toString());
    }
}