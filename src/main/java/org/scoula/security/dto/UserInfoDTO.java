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
    private String user_name;
    private String address;
    private String birthdate;
    private List<String> roles;

    public static UserInfoDTO of(MemberDTO dto) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return new UserInfoDTO(
                dto.getUserId(),
                dto.getUserName(),
                dto.getAddress(),
                formatter.format(dto.getBirthdate()),
                dto.getAuthList().stream()
                        .map(AuthDTO::getAuth)
                        .toList()
        );
    }
}


