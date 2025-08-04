package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AccountConnectDTO {
    @ApiModelProperty(
            value = "인터넷 뱅킹 ID",
            example = "honni7007",
            required = true
    )
    private String id;

    @ApiModelProperty(
            value = "Codef 은행 기관 코드",
            example = "0008",
            required = true
    )
    private String organization;

    @ApiModelProperty(
            value = "Codef 은행 기관명",
            example = "신한",
            required = true
    )
    @JsonProperty("bank_name")
    private String bankName;


    @ApiModelProperty(
            value = "인터넷 뱅킹 Password",
            example = "인터넷 뱅킹 Password",
            required = true
    )
    private String password;
}
