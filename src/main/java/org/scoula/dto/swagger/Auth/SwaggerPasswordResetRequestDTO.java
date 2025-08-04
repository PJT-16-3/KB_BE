package org.scoula.dto.swagger.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerPasswordResetRequestDTO {
    @JsonProperty("user_id")
    @ApiModelProperty(value = "비밀번호를 변경할 사용자 ID", required = true, example = "test@example.com")
    private String userId;

    @JsonProperty("new_password")
    @ApiModelProperty(value = "새 비밀번호", required = true, example = "NewP@ssword123")
    private String newPassword;
}
