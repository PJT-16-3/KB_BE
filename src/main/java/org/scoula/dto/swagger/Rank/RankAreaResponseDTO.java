package org.scoula.dto.swagger.Rank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "면적별 청약 순위 결과 응답 DTO")
public class RankAreaResponseDTO {

    @ApiModelProperty(
            value = "면적별 청약 순위 결과",
            example = "{ \"85 이하\": \"1순위\", \"102 이하\": \"1순위\", \"135 이하\": \"2순위\", \"모든 면적\": \"2순위\" }"
    )
    private Map<String, String> rankByArea;
}