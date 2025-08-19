package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.PythonAptRequestDTO;
import org.scoula.dto.PythonOfficetelRequestDTO;
import org.scoula.dto.ScoreRecord;
import org.scoula.dto.AptInfo;

import java.util.List;

@Mapper
public interface ProbabilityMapper {
    List<ScoreRecord> selectScoreRecords(@Param("sido") String sido,
                                         @Param("sigungu") String sigungu,
                                         @Param("residentCode") int residentCode);

    AptInfo selectAptInfo(@Param("aptIdx") int aptIdx);

    PythonAptRequestDTO getPythonAptInfoByPblancNo(@Param("pblancNo") String pblancNo);

    PythonOfficetelRequestDTO getPythonOfficetelInfoByPblancNo(@Param("pblancNo") String pblancNo);
}
