package org.scoula.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AlarmMessageRequest {
    @ApiModelProperty(value = "푸쉬 알람 제목",example ="📢 새로운 공고가 도착 했습니다!")
    private String title;
    @ApiModelProperty(value = "푸쉬 알람 내용",example ="2025-08-17 부로 새로운 청약 공고가 도착 했습니다!")
    private String body;
}
