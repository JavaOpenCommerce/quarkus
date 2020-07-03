package com.example.database.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"images"})
public class ItemDetails {

    private Long id;
    private String name;
    private Locale lang;
    private String description;

    @Builder.Default
    private Set<Image> images = new HashSet<>();

}
