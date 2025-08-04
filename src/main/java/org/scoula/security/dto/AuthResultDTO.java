package org.scoula.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResultDTO{
    @JsonProperty("access_token")
    String accesstoken;
    @JsonProperty("refresh_token")
    String refreshToken;
    UserInfoDTO user;
}
