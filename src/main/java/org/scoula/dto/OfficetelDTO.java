package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.YearMonth;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficetelDTO {

    @JsonProperty("HOUSE_MANAGE_NO")
    private String houseManageNo;

    @JsonProperty("PBLANC_NO")
    private String pblancNo;

    @JsonProperty("HOUSE_NM")
    private String houseNm;

    @JsonProperty("HOUSE_SECD")
    private String houseSecd;

    @JsonProperty("HOUSE_SECD_NM")
    private String houseSecdNm;

    @JsonProperty("HOUSE_DTL_SECD")
    private String houseDtlSecd;

    @JsonProperty("HOUSE_DTL_SECD_NM")
    private String houseDtlSecdNm;

    @JsonProperty("SEARCH_HOUSE_SECD")
    private String searchHouseSecd;

    @JsonProperty("SUBSCRPT_AREA_CODE")
    private String subscrptAreaCode;

    @JsonProperty("SUBSCRPT_AREA_CODE_NM")
    private String subscrptAreaCodeNm;

    @JsonProperty("HSSPLY_ADRES")
    private String hssplyAdres;

    @JsonProperty("TOT_SUPLY_HSHLDCO")
    private Integer totSuplyHshldco;

    @JsonProperty("RCRIT_PBLANC_DE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rcritPblancDe;

    @JsonProperty("SUBSCRPT_RCEPT_BGNDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rceptBgnde;

    @JsonProperty("SUBSCRPT_RCEPT_ENDDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rceptEndde;

    @JsonProperty("PRZWNER_PRESNATN_DE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate przwnerPresnatnDe;

    @JsonProperty("CNTRCT_CNCLS_BGNDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cntrctCnclsBgnde;

    @JsonProperty("CNTRCT_CNCLS_ENDDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cntrctCnclsEndde;

    @JsonProperty("HMPG_ADRES")
    private String hmpgAdres;

    @JsonProperty("BSNS_MBY_NM")
    private String bsnsMbyNm;

    @JsonProperty("MDHS_TELNO")
    private String mdhsTelno;

    @JsonProperty("MVN_PREARNGE_YM")
    private String mvnPrearngeYm;

    @JsonProperty("PBLANC_URL")
    private String pblancUrl;

    private Integer officetelIdx;

    private Double latitude;
    private Double longitude;
}


