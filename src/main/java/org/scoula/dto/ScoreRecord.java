package org.scoula.dto;

import lombok.Data;

@Data
public class ScoreRecord {
    private double minScore;
    private double avgScore;
    private double maxScore;
    private int supplyCount;
    private int residentCode;
}
