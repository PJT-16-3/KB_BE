package org.scoula.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AllHouseListDTO {
    private String houseNm;
    private String hssplyAdres;
    private String pblancNo;
    private String applicationPeriod;
    private String si;
    private String sigungu;
    private String houseType;
    private Integer minArea;
    private Integer maxArea;
    private Long minPrice;
    private Long maxPrice;
    private Double latitude;
    private Double longitude;
}
