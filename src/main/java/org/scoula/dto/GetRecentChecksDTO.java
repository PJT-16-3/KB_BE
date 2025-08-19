package org.scoula.dto;

import lombok.Data;

@Data
public class GetRecentChecksDTO {
    private Integer usersIdx;
    private String pblancNo;
    private String houseType;
}
