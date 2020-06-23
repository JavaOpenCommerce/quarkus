package com.example.database.entity;


import com.example.utils.LocaleConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.Locale;
import java.util.Set;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "item")
public class ItemDetails extends BaseEntity{

    private String name;

    @Convert(converter = LocaleConverter.class)
    private Locale lang;

    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Image> images;

}
