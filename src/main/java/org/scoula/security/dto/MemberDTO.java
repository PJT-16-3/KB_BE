package org.scoula.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    @ApiModelProperty(hidden = true)
    private int usersIdx;
    @ApiModelProperty(
            value   = "유저 아이디 입력",
            required= true,
            example = "test@example.com"
    )
    @JsonProperty("user_id")            //users.user_idx
    private String userId;             // users.user_id

    @ApiModelProperty(
            value   = "유저 이름 입력",
            required= true,
            example = "test 유저 이름"
    )
    @JsonProperty("user_name")
    private String userName;           // users.user_name

    @ApiModelProperty(
            value   = "user password 입력",
            required= true,
            example = "P@ss1234"
    )
    private String password;           // users.password

    @ApiModelProperty(
            value   = "address",
            required= true,
            example = "서울특별시 강남구"
    )
    private String address;

    @ApiModelProperty(
            value   = "생일 입력",
            required= true,
            example = "2001-01-01"
    )
    private Date birthdate;            // users.birthdate

    @ApiModelProperty(hidden = true)
    private List<AuthDTO> authList;

    @ApiModelProperty(hidden = true)
    private Long kakaoUserId;
}
