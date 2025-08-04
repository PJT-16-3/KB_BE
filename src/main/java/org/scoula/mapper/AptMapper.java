package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AptMapper {
    void insertApt(AptDTO aptDTO);
    List<AptIdxDTO> getIdxAndHouseMangeNo();
    void insertAptType(AptTypeDTO aptTypeResponseDTO);
    void deleteOld(@Param("firstDayOfMonth") LocalDate firstDayOfMonth);

    void insertOfficetel(OfficetelDTO officetelDTO);
    List<OfficetelIdxDTO>getIdxAndHouseManageNoFromOfficetel();
    void insertOfficetelType(OfficetelTypeDTO officetelTypeDTO);
    void deleteOldFromOfficetel(@Param("firstDayOfMonth") LocalDate firstDayOfMonth);

    AptDetailDTO getAptDetails(String pblanc_no);
    OfficetelDetailDTO getOfficetelDetails(String pblanc_no);

    int findAptIdxByPblancNo(@Param("pblancNo") String pblancNo);
    int findOfficetelIdxByPblancNo(@Param("pblancNo") String pblancNo);

    List<AptDTO> findAllAptLocations();
    List<OfficetelDTO> findAllOfficetelLocations();

    int incrementAptViewCount(@Param("pblancNo") String pblancNo);
    int incrementOfficetelViewCount(@Param("pblancNo") String pblancNo);

    List<InfraPlaceDTO> getInfraPlace(@Param("aptIdx") int aptIdx);
    List<InfraPlaceDTO> getOfficetelInfraPlace(@Param("officetelIdx") int officetelIdx);



}
