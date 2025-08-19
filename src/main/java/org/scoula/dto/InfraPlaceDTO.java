package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InfraPlaceDTO {
    @JsonProperty("place_type")
    private String placeType;
    @JsonProperty("place_name")
    private String placeName;
    @JsonProperty("distance")
    private Double distance;
    @JsonProperty("road_address_name")
    private String roadAddressName;
    private String longitude;
    private String latitude;
}
