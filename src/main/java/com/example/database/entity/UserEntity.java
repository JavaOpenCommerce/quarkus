package com.example.database.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserEntity {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @Builder.Default
    private Set<UserType> permissions = new HashSet<>();

    @Builder.Default
    private Set<Address> addresses = new HashSet<>();

}
