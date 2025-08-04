package org.scoula.dto.swagger.Email;



import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "이메일 인증 코드 발송 요청 DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwaggerSendVerificationCode {

    @ApiModelProperty(
            value = "인증 코드를 발송할 이메일 주소",
            example = "user@example.com",
            required = true
    )
    @JsonProperty("user_id")
    private String userId;
}
