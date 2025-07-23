package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.dto.ChungyakAccountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccountMapper {

    void insertChungyakAccount(@Param("dto") ChungyakAccountDTO dto,
                               @Param("userIdx") int userIdx,
                               @Param("isPayment") boolean isPayment);

    List<ChungyakAccountDTO> findAccountsByUserIdx(@Param("userIdx") int userIdx);

    String findEarliestAccountStartDate(@Param("userIdx") int userIdx);
}
