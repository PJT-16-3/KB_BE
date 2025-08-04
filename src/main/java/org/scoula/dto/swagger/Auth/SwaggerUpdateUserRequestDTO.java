package org.scoula.dto.swagger.Auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "회원정보 수정 요청 DTO")
public class SwaggerUpdateUserRequestDTO {

    @ApiModelProperty(value = "사용자 이름", example = "홍길순")
    private String userName;

    @ApiModelProperty(value = "주소", example = "부산광역시 해운대구")
    private String address;
}
