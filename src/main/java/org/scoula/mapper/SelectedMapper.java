package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.*;

import java.util.List;

@Mapper
public interface SelectedMapper {

    void updateHomePrice(@Param("userIdx") int userIdx, @Param("homePrice") HomePriceDTO homePrice);

    void deleteSelectedRegion(int userInfoIdx);
    void deleteSelectedHomeSize(int userInfoIdx);
    void deleteSelectedHomeType(int userInfoIdx);

    void insertSelectedRegion(@Param("userIdx") int userInfoIdx, @Param("region") RegionDTO region);
    void insertSelectedHomeType(@Param("userIdx") int userInfoIdx, @Param("hometype") HomeTypeDTO hometype);

    HomePriceDTO selectHomePriceByUserIdx(int userIdx);
    int findUserInfoIdxByUserIdx(int userIdx);

    List<RegionDTO> selectSelectedRegion(int userInfoIdx);
    List<HomeSizeDTO> selectSelectedHomesize(int userInfoIdx);
    List<HomeTypeDTO> selectSelectedHometype(int userInfoIdx);

    int getUserRankByUserInfoIdx(int userInfoIdx);

    int upsertSelectedHomeSize(@Param("userInfoIdx") int userInfoIdx,
                               @Param("homeSize") HomeSizeDTO homeSize,
                               @Param("userRank") int userRank);

    Integer getUserRankByUsersIdx(@Param("usersIdx") int usersIdx);
}