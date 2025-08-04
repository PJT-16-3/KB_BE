package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "희망 최소·최대 가격 DTO")
public class HomePriceDTO {

    @ApiModelProperty(value = "희망 최소 가격", example = "300000000", required = true)
    @JsonProperty("hope_min_price")
    private int hopeMinPrice;

    @ApiModelProperty(value = "희망 최대 가격", example = "500000000", required = true)
    @JsonProperty("hope_max_price")
    private int hopeMaxPrice;
}
