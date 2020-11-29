package com.example.rest.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {

    private Long id;
    private Long userId;
    private String street;
    private String local;
    private String city;
    private String zip;
}
