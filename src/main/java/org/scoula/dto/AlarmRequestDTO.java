package org.scoula.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Alarm Request DTO")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AlarmRequestDTO {
    @ApiModelProperty(value = "FCM Token",example ="FCM토큰을 입력 ")
    private String fcmToken;
}