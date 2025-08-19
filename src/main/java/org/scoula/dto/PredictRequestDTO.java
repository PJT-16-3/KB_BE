package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictRequestDTO {
    @JsonProperty("gnsply_hshldco")
    private int gnsplyHshldco;         // 일반공급세대수

    @JsonProperty("spsply_hshldco")
    private int spsplyHshldco;         // 특별공급세대수

    @JsonProperty("si")
    private String si;                 // 시도

    @JsonProperty("sigungu")
    private String sigungu;            // 시군구

    @JsonProperty("tot_supy_hshldco")
    private int totSuplyHshldco;       // 공급규모 (일반 + 특별)

    @JsonProperty("suply_hshldco")
    private int suplyHshldco;          // 공급세대수

    @JsonProperty("house_rank")
    private int houseRank;            // 순위

    @JsonProperty("reside_secd")
    private int resideSecd;            // 거주코드

    @JsonProperty("score")
    private int score;                 // 점수
}

//id BIGINT AUTO_INCREMENT PRIMARY KEY,
//si VARCHAR(50),                 -- 시도
//sigungu VARCHAR(50),            -- 시군구
//suply_hshldco INT,              -- 공급세대수
//gnsply_hshldco INT,             -- 일반공급세대수
//spsply_hshldco INT,             -- 특별공급세대수
//tot_suply_hshldco INT,          -- 공급규모 (일반 + 특별)
//rank INT,                       -- 순위 (1: 1순위, 2: 2순위)
//reside_secd VARCHAR(10),        -- 거주코드 (1: 해당지역, 2~3: 기타지역)
//win_probability FLOAT,          -- 당첨확률 (예측 결과)
//min_score FLOAT,                -- 최저당첨가점
//avg_score FLOAT,                -- 평균당첨가점
//max_score FLOAT                 -- 최고당첨가점