package com.example.rest.dtos;

import lombok.*;

import java.util.Locale;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    private Long id;
    private String categoryName;
    private String description;
    private Locale lang;
}
