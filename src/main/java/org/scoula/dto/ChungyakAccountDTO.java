package org.scoula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChungyakAccountDTO {
    private String accountDisplay;
    private String accountBalance;
    private String accountStartDate;
    private String resAccount;
    private String resAccountName;
    private String bankName;
}
