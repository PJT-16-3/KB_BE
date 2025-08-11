package org.scoula.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.dto.AllHouseListDTO;
import org.scoula.dto.PreferenceDTO;
import org.scoula.dto.home.HouseListDTO;

import java.util.List;

public interface HouseListMapper {
    List<HouseListDTO> getAllHouseList(@Param("userIdx") int userIdx);

    List<HouseListDTO> getRecommendationList(PreferenceDTO preference);

    AllHouseListDTO getHouseDetailByPblancNo(@Param("pblancNo") String pblancNo);

}
