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

import java.util.HashMap;
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
        HashMap<Integer, HouseListDTO> map = new HashMap<>();
        for (HouseListDTO house : houseList) {
            String pblancNo = house.getPblancNo();
            PredictRequestDTO reqDTO = new PredictRequestDTO();
            // 아파트, 오피 똑같이 들어가는 값
            reqDTO.setScore(userInfo.getTotalGaScore());
            reqDTO.setSi(house.getSi());
            reqDTO.setSigungu(house.getSigungu());

            if (house.getHouseType().equals("APT") || house.getHouseType().equals("신혼희망타운")) {
                int aptIdx = aptMapper.findAptIdxByPblancNo(pblancNo);
                AptDetailDTO aptDetail = aptMapper.getAptDetails(pblancNo);
                AptTypeDTO aptType = aptMapper.getAptType(aptIdx);

                reqDTO.setGnsplyHshldco(aptType.getSuplyHshldco());
                reqDTO.setSpsplyHshldco(aptType.getSpsplyHshldco());
                reqDTO.setTotSuplyHshldco(aptType.getSuplyHshldco() + aptType.getSpsplyHshldco());
                reqDTO.setSuplyHshldco(aptType.getSuplyHshldco() + aptType.getSpsplyHshldco());
                reqDTO.setHouse_rank(1);    // 이거 db에서 값 받아와야함
                if (userMapper.findUserRegionByIdx(usersIdx).startsWith(aptMapper.findRegionByAptIdx(pblancNo))) {
                    reqDTO.setResideSecd(1);
                } else {
                    reqDTO.setResideSecd(2);
                }
            } else {
                int officetelIdx = aptMapper.findOfficetelIdxByPblancNo(pblancNo);
                OfficetelDetailDTO officetelDetail = aptMapper.getOfficetelDetails(pblancNo);
                OfficetelTypeDTO officeType = aptMapper.getOfficetelType(officetelIdx);
                reqDTO.setTotSuplyHshldco(officetelDetail.getTotSuplyHshldco());
                reqDTO.setSuplyHshldco(officeType.getSuplyHshldco());
                if (userMapper.findUserRegionByIdx(usersIdx).startsWith(aptMapper.findRegionByOfficetelIdx(pblancNo))) {
                    reqDTO.setResideSecd(1);
                } else {
                    reqDTO.setResideSecd(2);
                }
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
//            "reside_secd": 1, 거주코드
//            "score": 30
//    }

    public double getWinProbability(PredictRequestDTO reqDTO) {

    }

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
