package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChungyakAccountDTO {
    @JsonProperty("account_display")
    private String accountDisplay;

    @JsonProperty("account_balance")
    private String accountBalance;

    @JsonProperty("account_start_date")
    private String accountStartDate;

    @JsonProperty("res_account")
    private String resAccount;

    @JsonProperty("res_account_name")
    private String resAccountName;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("res_final_round_no")
    private String resFinalRoundNo;

    @JsonProperty("res_account_tr_date")
    private String resAccountTrDate;

    @JsonProperty("is_payment")
    private boolean isPayment;
}
