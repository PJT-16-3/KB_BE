package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "선택 주택 면적 DTO")
public class HomeSizeDTO {

    @ApiModelProperty(value = "최소 주택 면적(㎡)", example = "60", required = true)
    @JsonProperty("min_homesize")
    private int minHomesize;

    @ApiModelProperty(value = "최대 주택 면적(㎡)", example = "85", required = true)
    @JsonProperty("max_homesize")
    private int maxHomesize;
}
