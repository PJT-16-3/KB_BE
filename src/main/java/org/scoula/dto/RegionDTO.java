package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "선택 지역 DTO")
public class RegionDTO {

    @ApiModelProperty(value = "시·도", example = "서울특별시", required = true)
    private String si;

    @ApiModelProperty(value = "구·군", example = "성북구", required = true)
    @JsonProperty("gun_gu")
    private String gunGu;
}
