package org.scoula.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSelectedDTO {

    @JsonProperty("user_info")
    private HomePriceDTO homePrice = new HomePriceDTO();

    @JsonProperty("selected_region")
    private List<RegionDTO> selectedRegion = new ArrayList<>();

    @JsonProperty("selected_homesize")
    private List<HomeSizeDTO> selectedHomesize = new ArrayList<>();

    @JsonProperty("selected_hometype")
    private List<HomeTypeDTO> selectedHometype = new ArrayList<>();
}