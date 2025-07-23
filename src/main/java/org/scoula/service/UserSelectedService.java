package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.*;
import org.scoula.mapper.SelectedMapper;
import org.scoula.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserSelectedService {

    private final SelectedMapper selectedMapper;
    private final UserMapper userMapper;

    @Transactional
    public void saveAllPreferences(String userId, UserSelectedDTO dto) {
        int usersIdx = userMapper.findUserIdxByUserId(userId);

        selectedMapper.updateHomePrice(usersIdx, dto.getHomePrice());

        int userInfoIdx = selectedMapper.findUserInfoIdxByUserIdx(usersIdx);

        selectedMapper.deleteSelectedRegion(userInfoIdx);
        selectedMapper.deleteSelectedHomeSize(userInfoIdx);
        selectedMapper.deleteSelectedHomeType(userInfoIdx);

        for (RegionDTO region : dto.getSelectedRegion()) {
            selectedMapper.insertSelectedRegion(userInfoIdx, region);
        }

        for (HomeSizeDTO homeSize : dto.getSelectedHomeSize()) {
            selectedMapper.insertSelectedHomeSize(userInfoIdx, homeSize);
        }

        for (HomeTypeDTO homeType : dto.getSelectedHomeType()) {
            selectedMapper.insertSelectedHomeType(userInfoIdx, homeType);
        }
    }

    public UserSelectedDTO getUserSelected(String userId) {
        int usersIdx = userMapper.findUserIdxByUserId(userId);
        int userInfoIdx = selectedMapper.findUserInfoIdxByUserIdx(usersIdx);

        HomePriceDTO homePrice = selectedMapper.selectHomePriceByUserIdx(usersIdx);
        List<RegionDTO> regions = selectedMapper.selectSelectedRegion(userInfoIdx);
        List<HomeSizeDTO> homesizes = selectedMapper.selectSelectedHomesize(userInfoIdx);
        List<HomeTypeDTO> hometypes = selectedMapper.selectSelectedHometype(userInfoIdx);

        return new UserSelectedDTO(homePrice, regions, homesizes, hometypes);
    }
}
