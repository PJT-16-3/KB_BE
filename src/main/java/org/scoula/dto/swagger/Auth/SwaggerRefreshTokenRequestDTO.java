package org.scoula.dto.swagger.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Access Token 재발급을 위한 요청 DTO")
@Data
public class SwaggerRefreshTokenRequestDTO {

    @ApiModelProperty(
            value   = "클라이언트가 보유한 Refresh Token",
            required= true,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    @JsonProperty("refresh_token")
    private String refreshToken;
}
