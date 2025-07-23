package org.scoula.dto;

import lombok.Data;

@Data
public class AccountConnectDTO {
    private String id;
    private String organization;
    private String bankName;
    private String password;
}
