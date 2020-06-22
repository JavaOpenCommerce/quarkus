package com.example.utils.converters;

import com.example.business.Value;
import com.example.business.Vat;
import com.example.business.models.CategoryModel;
import com.example.business.models.ItemModel;
import com.example.database.entity.Item;
import com.example.database.entity.ItemDetails;
import com.example.rest.dtos.ItemDto;

import java.util.Set;
import java.util.stream.Collectors;

public interface ItemConverter {


    static ItemModel convertToModel(Item item, String lang, String defaultLang) {

        Set<CategoryModel> categoryModels = item.getCategory()
                .stream()
                .map(category -> CategoryConverter.convertToModel(category))
                .collect(Collectors.toSet());

        ItemDetails details = ItemDetailConverter.getItemDetailsByLanguage(item, lang, defaultLang);

        return ItemModel.builder()
                .id(item.getId())
                .name(details.getName())
                .valueGross(Value.of(item.getValueGross()))
                .producer(ProducerConverter.convertToModel(item.getProducer()))
                .category(categoryModels)
                .vat(Vat.of(item.getVat()))
                .image(ImageConverter.convertToModel(item.getImage()))
                .build();
    }

    static ItemDto convertToDto(ItemModel itemModel) {
        return ItemDto.builder()
                .id(itemModel.getId())
                .name(itemModel.getName())
                .valueGross(itemModel.getValueGross().asDecimal())
                .producer(ProducerConverter.convertToDto(itemModel.getProducer()))
                .vat(itemModel.getVat().asDouble())
                .image(ImageConverter.convertToDto(itemModel.getImage()))
                .build();
    }
}
