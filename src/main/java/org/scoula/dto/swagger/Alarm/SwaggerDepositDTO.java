package org.scoula.dto.swagger.Alarm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerDepositDTO {
    @ApiModelProperty(value = "알림 제목", example = "📢 이번 달 예치금이 미납 되었습니다")
    String title;
    @ApiModelProperty(value = "알림 내용", example = "이번 달 예치금이 미납 되었습니다. 확인해 주세요 😊")
    String content;
}
