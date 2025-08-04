package org.scoula.dto.swagger.UserFavorite;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerAddFavoriteRequestDTO {

    @ApiModelProperty(value = "주택 유형 (APT, 신혼희망타운 등)", example = "APT", required = true)
    @JsonProperty("house_type")
    private String houseType;

    @ApiModelProperty(value = "공고 번호", example = "2025000271", required = true)
    @JsonProperty("pblanc_no")
    private String pblancNo;
}
