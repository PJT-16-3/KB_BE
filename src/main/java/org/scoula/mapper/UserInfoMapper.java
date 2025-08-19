package org.scoula.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.dto.RegionDTO;
import org.scoula.dto.UserInfoDTO;

import java.util.List;

public interface UserInfoMapper {
    UserInfoDTO getUserInfoByUsersIdx(@Param("usersIdx") int usersIdx);
    List<RegionDTO> findSelectedSiGunGuByUserInfoIdx(@Param("userInfoIdx") int userInfoIdx);
    Integer findSelectedMinHomeSizeByUserInfoIdx(@Param("userInfoIdx") int userInfoIdx);
    Integer findSelectedMaxHomeSizeByUserInfoIdx(@Param("userInfoIdx") int userInfoIdx);
    List<String> findSelectedHomeTypeByUserInfoIdx(@Param("userInfoIdx") int userInfoIdx);

}
