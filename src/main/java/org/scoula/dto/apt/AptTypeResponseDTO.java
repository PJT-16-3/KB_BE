package org.scoula.dto.apt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AptTypeResponseDTO {
    private Integer currentCount;
    private List<AptTypeDTO> data;;
}
