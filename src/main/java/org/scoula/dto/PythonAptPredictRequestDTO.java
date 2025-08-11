package org.scoula.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data
@NoArgsConstructor // ★ MyBatis 세터 매핑용
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PythonAptPredictRequestDTO {
    private String pblancNo;            // a.pblanc_no
    private String si;                  // a.si
    private String sigungu;             // a.sigungu
    private String subscrptAreaCodeNm;  // a.subscrpt_area_code_nm

    private Integer gnsplyHshldco;      // SUM(...) -> CAST로 정수화
    private Integer spsplyHshldco;
    private Integer totSuplyHshldco;
    private Integer suplyHshldco;
    private Integer reside_secd;
    // 선택 필드(있으면 매핑)
    private Integer houseRank;          // sh.user_rank
    private Integer score;              // ui.total_ga_score
}
