package org.scoula.dto.swagger.Alarm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerChungyakAlarmDTO {

    @ApiModelProperty(value = "알림 제목", example = "청약 접수 시작 알림")
    private String title;

    @ApiModelProperty(value = "알림 내용", example = "2025년 특별공급 청약 접수가 오늘부터 시작됩니다.")
    private String content;

    @ApiModelProperty(value = "관련 링크", example = "https://www.applyhome.co.kr")
    private String link;

    @ApiModelProperty(value = "주택 유형", example = "APT or 오피스텔")
    private String houseType;
}
