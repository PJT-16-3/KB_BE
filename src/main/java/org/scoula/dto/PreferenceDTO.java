package org.scoula.dto;

import lombok.Data;

import java.util.List;

@Data
public class PreferenceDTO {
    private int usersIdx;
    private List<RegionDTO> siGunGu;
    private int minHomeSize;
    private int maxHomeSize;
    private int userRank;
    private List<String> homeType;
    private int hopeMinPrice;
    private int hopeMaxPrice;
    private int maritalStatus;
    private int totalGaScore;
}

//preference.setUsersIdx(usersIdx);
//        preference.setSi(si);
//        preference.setGunGu(gunGu);
//        preference.setSelectedHomeSize(selectedHomeSize);
//        preference.setHopeMaxPrice(userInfo.getHopeMaxPrice());
//        preference.setHopeMinPrice(userInfo.getHopeMinPrice());
//        preference.setMaritalStatus(userInfo.isMaritalStatus());
//        preference.setTotalGaScore(userInfo.getTotalGaScore());