package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class FavoriteRequestDTO {
    private int usersIdx;
    @JsonProperty("house_type")
    private String houseType;
    @JsonProperty("pblanc_no")
    private String pblancNo;
}
