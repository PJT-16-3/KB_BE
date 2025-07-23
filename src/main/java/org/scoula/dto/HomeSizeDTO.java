package org.scoula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeSizeDTO {
    private int minHomesize;
    private int maxHomesize;
}
