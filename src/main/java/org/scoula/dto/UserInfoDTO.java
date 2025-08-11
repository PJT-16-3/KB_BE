package org.scoula.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoDTO {
    private int usersIdx;
    private int hopeMaxPrice;             // 희망 최대 금액
    private int hopeMinPrice;             // 희망 최소 금액
    private int totalGaScore;             // 총 가점 점수
    private boolean maritalStatus = false;// 혼인 여부
}
