package org.scoula.dto.swagger.Email;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "이메일 인증 코드 검증 요청 DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerVerifyEmailCode {

    @ApiModelProperty(
            value = "인증 코드를 발송한 이메일 주소",
            example = "du123kim@naver.com",
            required = true
    )
    @JsonProperty("user_id")
    private String userId;

    @ApiModelProperty(
            value = "인증 코드",
            example = "1d23gfx2",
            required = true
    )
    private String code;
}