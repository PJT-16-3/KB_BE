package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.scoula.dto.ChungyakAccountDTO;

import java.time.LocalDate;

@Mapper
public interface AccountMapper {

    void insertChungyakAccount(@Param("dto") ChungyakAccountDTO dto,
                               @Param("userIdx") int userIdx,
                               @Param("isPayment") boolean isPayment);

    ChungyakAccountDTO findAccountByUserIdx(@Param("userIdx") int userIdx);

    LocalDate findAccountStartDate(@Param("userIdx") int userIdx);

    void deleteAccountByUserIdx(@Param("userIdx") int userIdx);

    Integer updateFcmToken (@Param("userIdx") int userIdx, @Param("fcmToken") String fcmToken);

}

