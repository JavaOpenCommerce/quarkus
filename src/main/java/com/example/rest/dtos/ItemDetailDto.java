package com.example.rest.dtos;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class ItemDetailDto {

    private Long id;
    private String name;
    private String description;
    private ImageDto mainImage;
    private ProducerDto producer;
    private List<ImageDto> additionalImages;
    private BigDecimal valueGross;
    private double vat;
    private int stock;
}
