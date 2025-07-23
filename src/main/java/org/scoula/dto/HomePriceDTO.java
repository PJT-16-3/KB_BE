package org.scoula.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePriceDTO {
    private int hopeMinPrice;
    private int hopeMaxPrice;
}
