package com.example.rest.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

    private Map<Long, ProductDto> products;
    private BigDecimal cardValueNett;
    private BigDecimal cardValueGross;
}
