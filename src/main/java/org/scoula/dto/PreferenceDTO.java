package org.scoula.dto;

import lombok.Data;

import java.util.List;

@Data
public class PreferenceDTO {
    private int usersIdx;
    private List<SelectedHomeSizeDTO> selectedHomeSize; // 희망 집 사이즈
    private List<String> selectedHouseSecd;     // 희망 집 타입
    private List<String> si;                    // 희망 지역 시
    private List<String> gunGu;                 // 희망 지역 군구
    private int hopeMaxPrice;             // 희망 최대 금액
    private int hopeMinPrice;             // 희망 최소 금액
    private int totalGaScore;             // 총 가점 점수
    private boolean maritalStatus = false;// 혼인 여부
    }
