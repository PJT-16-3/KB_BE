package org.scoula.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private String userId;             // users.user_id
    private String user_name;           // users.user_name
    private String password;           // users.password
    private String address;
    private String jwtRefreshToken;    // users.jwt_refresh_token
    private Date birthdate;            // users.birthdate
    private List<AuthDTO> authList;    // users_auth 연관된 권한 목록
}
