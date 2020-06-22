package com.example.utils.converters;

import com.example.business.models.ImageModel;
import com.example.business.models.ItemDetailModel;
import com.example.database.entity.Item;
import com.example.database.entity.ItemDetails;
import com.example.rest.dtos.ImageDto;
import com.example.rest.dtos.ItemDetailDto;

import java.util.Set;
import java.util.stream.Collectors;

public interface ItemDetailConverter {

    static ItemDetailModel convertToModel(Item item, String lang, String defaultLang) {

        ItemDetails details = getItemDetailsByLanguage(item, lang, defaultLang);

        Set<ImageModel> images = details.getImages().stream()
                .map(i -> ImageConverter.convertToModel(i))
                .collect(Collectors.toSet());

        return ItemDetailModel.builder()
                .id(item.getId())
                .valueGross(item.getValueGross())
                .vat(item.getVat())
                .producer(ProducerConverter.convertToModel(item.getProducer()))
                .stock(item.getStock())
                .mainImage(ImageConverter.convertToModel(item.getImage()))
                .name(details.getName())
                .description(details.getDescription())
                .additionalImages(images)
                .build();
    }

    static ItemDetailDto convertToDto(ItemDetailModel item) {

        Set<ImageDto> images = item.getAdditionalImages().stream()
                .map(i -> ImageConverter.convertToDto(i))
                .collect(Collectors.toSet());

        return ItemDetailDto.builder()
                .id(item.getId())
                .valueGross(item.getValueGross())
                .vat(item.getVat())
                .producer(ProducerConverter.convertToDto(item.getProducer()))
                .stock(item.getStock())
                .mainImage(ImageConverter.convertToDto(item.getMainImage()))
                .name(item.getName())
                .description(item.getDescription())
                .additionalImages(images)
                .build();
    }

    static ItemDetails getItemDetailsByLanguage(Item item, String lang, String defaultLang) {

        return item.getDetails().stream()
                .filter(d -> d.getLang().equalsIgnoreCase(lang))
                .findFirst()
                .orElse(item.getDetails().stream()
                        .filter(d -> d.getLang().equalsIgnoreCase(defaultLang))
                        .findFirst()
                        .orElse(ItemDetails.builder().name("Error").build()));
    }
}
