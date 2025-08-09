package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.*;
import org.scoula.mapper.AptMapper;
import org.scoula.mapper.HouseListMapper;
import org.scoula.mapper.UserInfoMapper;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecommendationService {
    private final PythonApiService pythonApiService;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;
    private final UserInfoMapper userInfoMapper;
    private final HouseListMapper houseListMapper;
    private final AptMapper aptMapper;

    public List<HouseListDTO> getRecommendationList(String token) {
        String username = jwtProcessor.getUsername(token);
        int usersIdx = userMapper.findUserIdxByUserId(username);

        UserInfoDTO userInfo = userInfoMapper.getUserInfoByUsersIdx(usersIdx);

        PreferenceDTO preference = getPreferenceInfo(usersIdx, userInfo);


        // 선호 정보에 맞는 청약 리스트
        List<HouseListDTO> houseList = houseListMapper.getRecommendationList(preference);

        for (HouseListDTO house : houseList) {
            String pblancNo = house.getPblancNo();
            PredictRequestDTO reqDTO = new PredictRequestDTO();
            if (house.getHouseType().equals("APT") || house.getHouseType().equals("신혼희망타운")) {
                int aptIdx = aptMapper.findAptIdxByPblancNo(pblancNo);
                AptDetailDTO aptDetail = aptMapper.getAptDetails(pblancNo);
                AptTypeDTO aptType = aptMapper.getAptType(aptIdx);

                reqDTO.setGnsplyHshldco(aptType.getSuplyHshldco());
                reqDTO.setSpsplyHshldco(aptType.getSpsplyHshldco());
                reqDTO.setSi(house.getSi());
                reqDTO.setSigungu(house.getSigungu());
                reqDTO.setTotSuplyHshldco(aptType.getSuplyHshldco() + aptType.getSpsplyHshldco());
                reqDTO.setSuplyHshldco(aptType.getSuplyHshldco() + aptType.getSpsplyHshldco());
                reqDTO.setHouse_rank(1);
                if (userMapper.findUserRegionByIdx(usersIdx).startsWith(aptMapper.findRegionByAptIdx(aptIdx))) {
                    reqDTO.setResideSecd(1);
                } else {
                    reqDTO.setResideSecd(2);
                }
                reqDTO.setScore(userInfo.getTotalGaScore());
            }
        }

        return houseList;
    }

//    {
//        "gnsply_hshidco": 80,
//            "spsply_hshidco": 10,
//            "si": "충청북도",
//            "sigungu": "충주시",
//            "tot_supy_hshldco": 3000,
//            "suply_hshldco": 60,
//            "house_rank": 1,
//            "reside_secd": 1,
//            "score": 30
//    }

    public PreferenceDTO getPreferenceInfo (int usersIdx, UserInfoDTO userInfo) {
        List<String> si = userInfoMapper.getSelectedSi(usersIdx);
        List<String> gunGu = userInfoMapper.getSelectedGunGu(usersIdx);
        List<SelectedHomeSizeDTO> selectedHomeSize = userInfoMapper.getSelectedHomeSize(usersIdx);

        PreferenceDTO preference = new PreferenceDTO();

        preference.setUsersIdx(usersIdx);
        preference.setSi(si);
        preference.setGunGu(gunGu);
        preference.setSelectedHomeSize(selectedHomeSize);
        preference.setHopeMaxPrice(userInfo.getHopeMaxPrice());
        preference.setHopeMinPrice(userInfo.getHopeMinPrice());
        preference.setMaritalStatus(userInfo.isMaritalStatus());
        preference.setTotalGaScore(userInfo.getTotalGaScore());

        return preference;
    }
}
