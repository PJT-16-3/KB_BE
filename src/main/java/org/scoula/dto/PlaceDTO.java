package org.scoula.dto;

import lombok.Data;

@Data
public class PlaceDTO {
    private Integer aptIdx;
    private Integer officetelIdx;
    private String placeType;
    private String placeName;
    private String address;
    private Integer distance;           // 거리 (meter)
    private double latitude;
    private double longitude;
    private String categoryName;
}
