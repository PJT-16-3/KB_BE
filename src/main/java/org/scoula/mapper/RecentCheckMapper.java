package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.GetRecentChecksDTO;
import org.scoula.dto.RecentCheckDTO;

import java.util.List;

@Mapper
public interface RecentCheckMapper {
    void insertRecentCheck(RecentCheckDTO recentCheck);

    List<GetRecentChecksDTO> getRecentChecks(@Param("usersIdx") Integer usersIdx);
}


