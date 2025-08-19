package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    @JsonProperty("user_info_idx")
    private int userInfoIdx;

    @JsonProperty("hope_max_price")
    private int hopeMaxPrice;

    @JsonProperty("hope_min_price")
    private int hopeMinPrice;

    @JsonProperty("total_ga_score")
    private int totalGaScore;

    @JsonProperty("marital_status")
    private int maritalStatus;
}
