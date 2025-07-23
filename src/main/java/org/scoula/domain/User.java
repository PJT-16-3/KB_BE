package org.scoula.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long usersIdx;         // users_idx
    private String userId;         // user_id
    private String userName;       // user_name
    private String password;       // password
    private String address;        // address
    private String jwtRefreshToken;// jwt_refresh_token
    private String kakaoUserId;    // kakao_user_id
    private LocalDate birthdate;   // birthdate
}

