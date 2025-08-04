package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AptDTO {

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

    @JsonProperty("RENT_SECD")
    private String rentSecd;

    @JsonProperty("RENT_SECD_NM")
    private String rentSecdNm;

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

    @JsonProperty("RCEPT_BGNDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rceptBgnde;

    @JsonProperty("RCEPT_ENDDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rceptEndde;

    @JsonProperty("SPSPLY_RCEPT_BGNDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate spsplyRceptBgnde;

    @JsonProperty("SPSPLY_RCEPT_ENDDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate spsplyRceptEndde;

    @JsonProperty("GNRL_RNK1_CRSPAREA_RCPTDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk1CrspareaRcptde;

    @JsonProperty("GNRL_RNK1_CRSPAREA_ENDDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk1CrspareaEndde;

    @JsonProperty("GNRL_RNK1_ETC_AREA_RCPTDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk1EtcAreaRcptde;

    @JsonProperty("GNRL_RNK1_ETC_AREA_ENDDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk1EtcAreaEndde;

    @JsonProperty("GNRL_RNK2_CRSPAREA_RCPTDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk2CrspareaRcptde;

    @JsonProperty("GNRL_RNK2_CRSPAREA_ENDDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk2CrspareaEndde;

    @JsonProperty("GNRL_RNK2_ETC_AREA_RCPTDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk2EtcAreaRcptde;

    @JsonProperty("GNRL_RNK2_ETC_AREA_ENDDE")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk2EtcAreaEndde;

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

    @JsonProperty("CNSTRCT_ENTRPS_NM")
    private String cnstrctEntrpsNm;

    @JsonProperty("MDHS_TELNO")
    private String mdhsTelno;

    @JsonProperty("BSNS_MBY_NM")
    private String bsnsMbyNm;

    @JsonProperty("MVN_PREARNGE_YM")
    private String mvnPrearngeYm;

    @JsonProperty("SPECLT_RDN_EARTH_AT")
    private String specltRdnEarthAt;

    @JsonProperty("MDAT_TRGET_AREA_SECD")
    private String mdatTrgetAreaSecd;

    @JsonProperty("PARCPRC_ULS_AT")
    private String parcprcUlsAt;

    @JsonProperty("PBLANC_URL")
    private String pblancUrl;

    private Double latitude;

    private Double longitude;

    private int aptIdx;
}
