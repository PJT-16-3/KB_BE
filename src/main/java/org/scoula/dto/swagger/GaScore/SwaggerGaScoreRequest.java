package org.scoula.dto.swagger.GaScore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwaggerGaScoreRequest {

    @ApiModelProperty(value = "세대주 여부 (1: 예, 0: 아니오)", example = "1", required = true)
    @JsonProperty("head_of_household")
    private int headOfHousehold;

    @ApiModelProperty(value = "주택 소유 여부 (1: 무주택, 0: 유주택)", example = "1", required = true)
    @JsonProperty("house_owner")
    private int houseOwner;

    @ApiModelProperty(value = "주택 처분 여부 (1: 처분함, 0: 안함)", example = "0", required = true)
    @JsonProperty("house_disposal")
    private int houseDisposal;

    @ApiModelProperty(value = "주택 처분일 (yyyy-MM, null 허용)", example = "null")
    @JsonProperty("disposal_date")
    private String disposalDate;

    @ApiModelProperty(value = "혼인 여부 (1: 기혼, 0: 미혼)", example = "1", required = true)
    @JsonProperty("marital_status")
    private int maritalStatus;

    @ApiModelProperty(value = "혼인 날짜 (yyyy-MM, null 허용)", example = "2015-05")
    @JsonProperty("wedding_date")
    private String weddingDate;

    @ApiModelProperty(value = "무주택 기간 (단위 : 년)", example = "6", required = true)
    @JsonProperty("no_house_period")
    private int noHousePeriod;

    @ApiModelProperty(value = "부양가족 수", example = "3", required = true)
    @JsonProperty("dependents_nm")
    private int dependentsNm;

    @ApiModelProperty()
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value = "birth_date")
    private String birthDate;

    @ApiModelProperty()
    @JsonProperty("residence_start_date")
    private String residenceStartDate;

}
