package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.HouseListDTO;
import org.scoula.mapper.HouseListMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class HouseService {
    private final HouseListMapper houseListMapper;

    public List<HouseListDTO> getAllHousingList(int userIdx) {
        try {
            return houseListMapper.getAllHouseList(userIdx);
        } catch (Exception e) {
            log.error("청약 공고 목록 조회 중 예외 발생", e);
            throw new IllegalStateException("청약 공고 목록을 불러오는 중 오류가 발생했습니다.", e);
        }
    }
}
