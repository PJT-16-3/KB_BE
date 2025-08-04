package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "선택 주택 유형 DTO")
public class HomeTypeDTO {

    @ApiModelProperty(value = "선택 주택 종류", example = "아파트", required = true)
    @JsonProperty("selected_house_secd")
    private String selectedHouseSecd;
}
