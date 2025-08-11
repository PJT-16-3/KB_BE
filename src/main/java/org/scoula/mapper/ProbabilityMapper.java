package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.PythonAptPredictRequestDTO;
import org.scoula.dto.ScoreRecord;
import org.scoula.dto.apt.AptInfo;

import java.util.List;

@Mapper
public interface ProbabilityMapper {
    List<ScoreRecord> selectScoreRecords(@Param("sido") String sido,
                                         @Param("sigungu") String sigungu,
                                         @Param("residentCode") int residentCode);

    AptInfo selectAptInfo(@Param("aptIdx") int aptIdx);

    PythonAptPredictRequestDTO selectPythonAptInfoByPblancNo(@Param("pblancNo") String pblancNo);
}
