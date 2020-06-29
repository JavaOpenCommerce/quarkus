package com.example.database.entity;

import com.example.utils.LocaleConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "items")
public class Producer extends BaseEntity {

    private String name;
    private String description;

    @Convert(converter = LocaleConverter.class)
    private Locale lang;

    @OneToOne
    @JoinColumn(name= "image_id")
    private Image image;

    @Builder.Default
    @ManyToMany(mappedBy = "producer")
    private Set<Item> items = new HashSet<>();
}