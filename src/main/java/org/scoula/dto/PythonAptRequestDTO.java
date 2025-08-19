package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class PythonAptRequestDTO {
    private Integer gnsplyHshldco;
    private Integer spsplyHshldco;
    private String si;
    private String sigungu;
    private Integer totSuplyHshldco;
    private Integer suplyHshldco;
    private Integer houseRank;
    private Integer resideSecd;
    private Integer score;
}


