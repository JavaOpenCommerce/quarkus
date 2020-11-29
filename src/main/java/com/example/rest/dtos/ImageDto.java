package com.example.rest.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    private Long id;
    private String alt;
    private String url;
}
