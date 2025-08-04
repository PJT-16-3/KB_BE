package org.scoula.dto.swagger.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.scoula.security.dto.AuthDTO;

import java.util.Date;
import java.util.List;

@Data
public class SwaggerUpdate {

    @ApiModelProperty(
            value   = "변경할 유저 이름 입력",
            required= true,
            example = "test 유저 이름"
    )
    @JsonProperty("user_name")
    private String userName;           // users.user_name

    @ApiModelProperty(
            value   = "변경된 address",
            required= true,
            example = "서울특별시 강남구"
    )
    private String address;

    @ApiModelProperty(
            value   = "변경된 생일 입력",
            required= true,
            example = "2001-01-01"
    )
    private Date birthdate;

}
