package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AptTypeDTO {
    @JsonProperty("HOUSE_TY")
    private String houseTy;

    @JsonProperty("SUPLY_AR")
    private Double suplyAr;

    @JsonProperty("SUPLY_HSHLDCO")
    private Integer suplyHshldco;

    @JsonProperty("SPSPLY_HSHLDCO")
    private Integer spsplyHshldco;

    @JsonProperty("LTTOT_TOP_AMOUNT")
    private String lttotTopAmount;

    @JsonIgnore
    private Integer aptIdx;
}
