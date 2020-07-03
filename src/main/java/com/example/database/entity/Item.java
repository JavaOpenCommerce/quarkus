package com.example.database.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Item {

    private Long id;
    private BigDecimal valueGross;
    private double vat;
    private int stock;
    private Image image;
    private Producer producer;

    @Builder.Default
    private Set<Category> category = new HashSet<>();

    @Builder.Default
    private Set<ItemDetails> details = new HashSet<>();

}
