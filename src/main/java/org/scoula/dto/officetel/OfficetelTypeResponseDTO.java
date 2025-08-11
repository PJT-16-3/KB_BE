package org.scoula.dto.officetel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficetelTypeResponseDTO {
    private Integer matchCount;
    private List<OfficetelTypeDTO> data;
}
