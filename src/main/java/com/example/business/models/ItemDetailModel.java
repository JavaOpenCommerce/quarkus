package com.example.business.models;

import com.example.business.Vat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Builder
@EqualsAndHashCode
public class ItemDetailModel {

    private Long id;
    private String name;
    private String description;
    private ImageModel mainImage;
    private ProducerModel producer;
    private Set<ImageModel> additionalImages;
    private BigDecimal valueGross;
    private Vat vat;
    private int stock;


}
