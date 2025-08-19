package org.scoula.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PythonOfficetelRequestDTO {

    private String si;

    private String sigungu;

    private Integer totSuplyHshldco;

    private Integer suplyHshldco;

    private Integer resideSecd = 0;
}
