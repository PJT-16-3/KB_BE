package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationListDTO {
    private Integer rank;
    @JsonProperty("favorite_count")
    private Integer favoriteCount;
    @JsonProperty("house_nm") // 공고명
    private String houseNm;
    @JsonProperty("hssply_adres") // 공급 주소
    private String hssplyAdres;
    @JsonProperty("pblanc_no")
    private String pblancNo;
    @JsonProperty("application_period") // 접수 기간 문자열 (예: 2025.07.31 ~ 2025.08.05)
    private String applicationPeriod;
    private String si;
    private String sigungu;
    @JsonProperty("house_type") // 공고유형 (APT, 신혼희망타운, 오피스텔, 도시형생활주택)
    private String houseType;
    @JsonProperty("min_area")
    private String minArea;
    @JsonProperty("max_area")
    private String maxArea;
    @JsonProperty("min_price")
    private String minPrice;
    @JsonProperty("max_price")
    private String maxPrice;
    @JsonProperty("is_favorite")
    private boolean isFavorite;
    private String latitude;
    private String longitude;
}
