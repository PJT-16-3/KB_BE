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
    private final RankService rankService;

    @Transactional
    public void saveAllPreferences(String userId, UserSelectedDTO dto) {
        int usersIdx = userMapper.findUserIdxByUserId(userId);

        selectedMapper.updateHomePrice(usersIdx, dto.getHomePrice());

        int userInfoIdx = selectedMapper.findUserInfoIdxByUserIdx(usersIdx);

        selectedMapper.deleteSelectedRegion(userInfoIdx);
        selectedMapper.deleteSelectedHomeType(userInfoIdx);

        List<RegionDTO> regions = dto.getSelectedRegion();
        if (regions != null) {
            for (RegionDTO region : regions) {
                selectedMapper.insertSelectedRegion(userInfoIdx, region);
            }
        }
        List<HomeTypeDTO> hometypes = dto.getSelectedHometype();
        if (hometypes != null) {
            for (HomeTypeDTO homeType : hometypes) {
                selectedMapper.insertSelectedHomeType(userInfoIdx, homeType);
            }
        }

        List<HomeSizeDTO> homesizes = dto.getSelectedHomesize();
        if (homesizes == null || homesizes.isEmpty() || homesizes.size() != 1) {
            throw new IllegalArgumentException("selected_homesize는 정확히 1개여야 합니다.");
        }
        HomeSizeDTO homesize = homesizes.get(0);

        int userRank = rankService.calculateRankForAreaOnly(
                usersIdx,
                homesize.getMinHomesize(),
                homesize.getMaxHomesize()
        );

        selectedMapper.upsertSelectedHomeSize(userInfoIdx, homesize, userRank);
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
