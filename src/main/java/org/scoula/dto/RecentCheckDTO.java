package org.scoula.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecentCheckDTO {
    private Integer usersIdx;
    private String pblancNo;
    private LocalDateTime viewedAt;
    private String houseType;
}
