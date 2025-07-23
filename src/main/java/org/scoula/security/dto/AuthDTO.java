package org.scoula.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO implements GrantedAuthority {
    private String username;  // users_auth.username
    private String auth;      // users_auth.auth (권한명: ROLE_USER 등)

    @Override
    public String getAuthority() {
        return "";
    }
}
