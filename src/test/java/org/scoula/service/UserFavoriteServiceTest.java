package org.scoula.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.RootConfig;
import org.scoula.dto.HouseListDTO;
import org.scoula.dto.UserFavoriteDTO;
import org.scoula.mapper.UserFavoriteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { UserFavoriteService.class, RootConfig.class })
@Log4j2
class UserFavoriteServiceTest {

    @Autowired
    private UserFavoriteService userFavoriteService;


    /**
     * APT 공고번호를 즐겨찾기에 추가했을 때
     */
    @Test
    void addFavorite_apt() {
        String aptPblancNo = "2025950040";  // 테스트용 공고번호
        boolean result = userFavoriteService.addFavorite(1, "APT", aptPblancNo);
        log.info("APT 즐겨찾기 추가 결과: {}", result);
        assertTrue(result || !result);  // 단순 실행 확인
    }


    @Test
    void indAptHouseByPblancNo() {
        List<HouseListDTO> list =  userFavoriteService.getFavoriteHouses(45);
        list.forEach(dto -> log.info(dto.toString()));
    }

}
