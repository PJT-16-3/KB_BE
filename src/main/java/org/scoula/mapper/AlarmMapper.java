package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.AlarmListDTO;
import org.scoula.dto.AlarmDetailDTO;

import java.util.List;

@Mapper
public interface AlarmMapper {

    // 목록 (필요 컬럼만)
    List<AlarmListDTO> getAlarmList(@Param("usersIdx") Integer usersIdx,
                                    @Param("onlyUnread") boolean onlyUnread);

    // 상세 (전부)
    AlarmDetailDTO getAlarmDetail(@Param("alarmIdx") Long alarmIdx,
                                  @Param("usersIdx") Integer usersIdx);

    // 생성/읽음처리/삭제 (필요 시)
    int insertDetail(AlarmDetailDTO alarm);
    int markRead(@Param("alarmIdx") Long alarmIdx, @Param("usersIdx") Integer usersIdx);
    int markAllRead(@Param("usersIdx") Integer usersIdx);
    int delete(@Param("alarmIdx") Long alarmIdx, @Param("usersIdx") Integer usersIdx);
    int deleteAllByUser(@Param("usersIdx") Integer usersIdx);
}
