package org.scoula.mapper;

import org.apache.ibatis.annotations.Param;
import org.scoula.dto.HouseListDTO;

import java.util.List;

public interface HouseListMapper {
    List<HouseListDTO> getAllHouseList(@Param("userIdx") int userIdx);
}
