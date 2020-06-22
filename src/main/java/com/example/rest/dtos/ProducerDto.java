package com.example.rest.dtos;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class ProducerDto {

    private Long id;
    private String name;
    private String description;
    private ImageDto image;
}
