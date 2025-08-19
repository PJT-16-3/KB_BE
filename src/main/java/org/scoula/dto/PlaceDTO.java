package org.scoula.dto;

import lombok.Data;

@Data
public class PlaceDTO {
    private Integer aptIdx;
    private Integer officetelIdx;
    private String placeType;
    private String placeName;
    private String address;
    private Long distance;
    private Double latitude;
    private Double longitude;
    private String categoryName;
}
