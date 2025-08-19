package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AptDetailDTO {
    @JsonIgnore
    private Integer aptIdx;
    @JsonProperty ("favorite_count")
    private Integer favoriteCount;
    @JsonProperty("view_count")
    private Integer viewCount;

    @JsonProperty("house_manage_no")
    private String houseManageNo;

    @JsonProperty("pblanc_no")
    private String pblancNo;

    @JsonProperty("house_nm")
    private String houseNm;

    @JsonProperty("house_secd")
    private String houseSecd;

    @JsonProperty("house_secd_nm")
    private String houseSecdNm;

    @JsonProperty("house_dtl_secd")
    private String houseDtlSecd;

    @JsonProperty("house_dtl_secd_nm")
    private String houseDtlSecdNm;

    @JsonProperty("search_house_secd")
    private String rentSecd;

    @JsonProperty("search_house_secd_nm")
    private String rentSecdNm;

    @JsonProperty("subscrpt_area_code")
    private String subscrptAreaCode;

    @JsonProperty("subscrpt_area_code_nm")
    private String subscrptAreaCodeNm;

    @JsonProperty("hssply_adres")
    private String hssplyAdres;

    @JsonProperty("tot_suply_hshldco")
    private Integer totSuplyHshldco;

    @JsonProperty("rcrit_pblanc_de")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rcritPblancDe;

    @JsonProperty("rcept_bgnde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rceptBgnde;

    @JsonProperty("rcept_endde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rceptEndde;

    @JsonProperty("spsply_rcept_bgnde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate spsplyRceptBgnde;

    @JsonProperty("spsply_rcept_endde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate spsplyRceptEndde;

    @JsonProperty("gnrl_rnk1_crsparea_rcptde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk1CrspareaRcptde;

    @JsonProperty("gnrl_rnk1_crsparea_endde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk1CrspareaEndde;

    @JsonProperty("gnrl_rnk1_etc_area_rcptde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk1EtcAreaRcptde;

    @JsonProperty("gnrl_rnk1_etc_area_endde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk1EtcAreaEndde;

    @JsonProperty("gnrl_rnk2_crsparea_rcptde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk2CrspareaRcptde;

    @JsonProperty("gnrl_rnk2_crsparea_endde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk2CrspareaEndde;

    @JsonProperty("gnrl_rnk2_etc_area_rcptde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk2EtcAreaRcptde;

    @JsonProperty("gnrl_rnk2_etc_area_endde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate gnrlRnk2EtcAreaEndde;

    @JsonProperty("przwner_presnatn_de")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate przwnerPresnatnDe;

    @JsonProperty("cntrct_cncls_bgnde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cntrctCnclsBgnde;

    @JsonProperty("cntrct_cncls_endde")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate cntrctCnclsEndde;

    @JsonProperty("hmpg_adres")
    private String hmpgAdres;

    @JsonProperty("cnstrct_entrps_nm")
    private String cnstrctEntrpsNm;

    @JsonProperty("mdhs_telno")
    private String mdhsTelno;

    @JsonProperty("bsns_mby_nm")
    private String bsnsMbyNm;

    @JsonProperty("mvn_prearnge_ym")
    private String mvnPrearngeYm;

    @JsonProperty("speclt_rdn_earth_at")
    private String specltRdnEarthAt;

    @JsonProperty("mdat_trget_area_secd")
    private String mdatTrgetAreaSecd;

    @JsonProperty("parcprc_uls_at")
    private String parcprcUlsAt;

    @JsonProperty("pblanc_url")
    private String pblancUrl;
    private String latitude;
    private String longitude;

    @JsonProperty("apt_type")
    private List<AptTypeDTO> aptType;

    @JsonProperty("infra_places")
    private List<InfraPlaceDTO> infraPlaces;
}
