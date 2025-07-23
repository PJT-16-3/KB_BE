package org.scoula.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private String username;
    private String email;
    private String birthdate;
    private List<String> roles;

    public static UserInfoDTO of(MemberDTO dto) {
        return new UserInfoDTO(
                dto.getUserId(),
                dto.getEmail(),
                dto.getBirthdate().toString(),
                dto.getAuthList().stream()
                        .map(AuthDTO::getAuth)
                        .toList()
        );
    }
}


