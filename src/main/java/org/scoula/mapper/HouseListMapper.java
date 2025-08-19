package org.scoula.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.dto.*;

import java.util.List;

public interface HouseListMapper {
    List<HouseListDTO> getAllHouseList(@Param("userIdx") int userIdx);

    List<RecommendationListDTO> getAptRecommendationList(
            @Param("region") RegionDTO region,
            @Param("preference") PreferenceDTO preference
    );

    List<RecommendationListDTO> getOfficetelRecommendationList(
            @Param("region") RegionDTO region,
            @Param("preference") PreferenceDTO preference
    );

    AllHouseListDTO getHouseDetailByPblancNo(@Param("pblancNo") String pblancNo);

}