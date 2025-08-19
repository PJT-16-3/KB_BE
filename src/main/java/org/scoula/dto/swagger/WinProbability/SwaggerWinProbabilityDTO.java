package org.scoula.dto.swagger.WinProbability;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "아파트 당첨 확률 예측 요청 DTO")
public class SwaggerWinProbabilityDTO {

    @ApiModelProperty(value = "청약 공고 번호", example = "202401010001", required = true)
    @JsonProperty("pblanc_no")
    private String pblancNo;
}
