package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.GaScoreDTO;


@Mapper
public interface GaScoreMapper {

    void updateScore(@Param("userIdx") int userIdx,
                     @Param("gaScoreDTO") GaScoreDTO gaScoreDTO);

    GaScoreDTO getScoresByUserIdx(@Param("userIdx") int userIdx);
}
