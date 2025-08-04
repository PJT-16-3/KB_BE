package org.scoula.dto.swagger.Auth;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerSignOutRequestDTO {

    @ApiModelProperty(value = "회원 탈퇴를 위한 회원 비밀번호 입력", required = true, example = "1234")
    private String password;
}
