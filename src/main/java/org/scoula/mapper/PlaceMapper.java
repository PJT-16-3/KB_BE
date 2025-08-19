package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.PlaceDTO;

@Mapper
public interface PlaceMapper {
    void insertHospital(PlaceDTO place);
    void insertSchool(PlaceDTO place);
    void insertMart(PlaceDTO place);
    void insertSubway(PlaceDTO place);
    void insertKindergarten(PlaceDTO place);
}