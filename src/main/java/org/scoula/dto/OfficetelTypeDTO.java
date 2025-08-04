package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficetelTypeDTO {
    @JsonProperty("HOUSE_MANAGE_NO")
    private String houseManageNo;
    @JsonProperty("PBLANC_NO")
    private String pblancNo;
    @JsonProperty("MODEL_NO")
    private String modelNo;
    @JsonProperty("TP")
    private String tp;
    @JsonProperty("EXCLUSE_AR")
    private Double excluseAr;
    @JsonProperty("SUPLY_HSHLDCO")
    private Integer suplyHshldco;
    @JsonProperty("SUPLY_AMOUNT")
    private Integer suplyAmount;
    @JsonProperty("SUBSCRPT_REQST_AMOUNT")
    private Integer subscrptReqstAmount;

    @JsonIgnore
    private Integer officetelIdx;


}
