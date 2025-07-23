package org.scoula.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private String user_id;
    private String birthdate;
    private List<String> roles;

    public static UserInfoDTO of(MemberDTO dto) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return new UserInfoDTO(
                dto.getUserId(),
                formatter.format(dto.getBirthdate()),
                dto.getAuthList().stream()
                        .map(AuthDTO::getAuth)
                        .toList()
        );
    }
}


