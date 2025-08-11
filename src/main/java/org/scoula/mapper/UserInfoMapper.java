package org.scoula.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.dto.SelectedHomeSizeDTO;
import org.scoula.dto.SelectedRegionDTO;
import org.scoula.dto.UserInfoDTO;

import java.util.List;

public interface UserInfoMapper {
    // userInfo 가져오기
    UserInfoDTO getUserInfoByUsersIdx(@Param("usersIdx") int usersIdx);

    // 선택 지역 가져오기
    List<SelectedRegionDTO> getSelectedRegion(@Param("userInfoIdx") int userInfoIdx);

    // 선택 지역 시 가져오기
    List<String> getSelectedSi(@Param("userInfoIdx") int userInfoIdx);

    // 선택 지역 시군구 가져오기
    List<String> getSelectedGunGu(@Param("userInfoIdx") int userInfoIdx);

    // 선택 집 사이즈
    List<SelectedHomeSizeDTO> getSelectedHomeSize(@Param("userInfoIdx") int userInfoIdx);

    // 선택 집 타입
    List<String> getSelectedHomeType(@Param("userInfoIdx") int userInfoIdx);
}