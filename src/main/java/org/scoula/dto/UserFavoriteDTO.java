package org.scoula.dto;

import lombok.Data;

@Data
public class UserFavoriteDTO {
    private int     userFavoriteIdx;       // PK
    private Integer usersIdx;              // FK → users.users_idx
    private String  aptPblanc;             // nullable
    private String  officePblanc;          // nullable
    private Float   predictedWinRate;
    private Float   predictedCutoffScore;
}
