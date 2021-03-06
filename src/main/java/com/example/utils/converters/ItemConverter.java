package com.example.utils.converters;

import com.example.business.Value;
import com.example.business.Vat;
import com.example.business.models.CategoryModel;
import com.example.business.models.ItemDetailModel;
import com.example.business.models.ItemModel;
import com.example.database.entity.*;
import dtos.ItemDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public interface ItemConverter {


    static ItemModel convertToModel(Item item, List<ItemDetails> itemDetails, List<Category> categories, Producer producer) {

        List<ItemDetailModel> details = itemDetails.stream()
                .map(ItemDetailConverter::convertToModel)
                .collect(Collectors.toList());

        List<CategoryModel> categoryModels = categories.stream()
                .map(CategoryConverter::convertToModel)
                .collect(Collectors.toList());

        return ItemModel.builder()
                .id(item.getId())
                .valueGross(Value.of(item.getValueGross()))
                .producer(
                        ProducerConverter.convertToModel(
                                ofNullable(producer).orElse(Producer.builder().build())))
                .category(categoryModels)
                .vat(Vat.of(item.getVat()))
                .details(details)
                .stock(item.getStock())
                .image(ImageConverter.convertToModel(
                        ofNullable(item.getImage()).orElse(Image.builder().build())))
                .build();
    }

    static ItemModel convertDtoToModel(ItemDto item) {
        return ItemModel.builder()
                .id(item.getId())
                .stock(item.getStock())
                .valueGross(Value.of(item.getValueGross()))
                .vat(Vat.of(item.getVat()))
                .image(ImageConverter.convertDtoToModel(item.getImage()))
                .build();
    }

    static Item convertModelToEntity(ItemModel item) {
        return Item.builder()
                .id(item.getId())
                .stock(item.getStock())
                .valueGross(item.getValueGross().asDecimal())
                .vat(item.getVat().asDouble())
                .image(ImageConverter.convertModelToEntity(item.getImage()))
                .categoryIds(emptyList())
                .details(emptyList())
                .build();
    }

    static List<ItemModel> convertToItemModelList(List<Item> items,
            List<ItemDetails> itemDetails,
            List<Category> categories,
            List<Producer> producers) {

        List<ItemModel> itemModels = new ArrayList<>();
        for (Item item : items) {
            List<Category> categoriesFiltered = categories.stream()
                    .filter(c -> item.getCategoryIds().contains(c.getId()))
                    .collect(toList());

            List<ItemDetails> itemDetailsFiltered = itemDetails.stream()
                    .filter(id -> id.getItemId() == item.getId())
                    .collect(toList());

            Producer producerRetrieved = producers.stream()
                    .filter(p -> p.getId() == item.getProducerId())
                    .findAny()
                    .orElse(Producer.builder().build());

            itemModels
                    .add(convertToModel(item, itemDetailsFiltered, categoriesFiltered, producerRetrieved));
        }
        return itemModels;
    }

    static ItemDto convertToDto(ItemModel item, String lang, String defaultLang) {

        ItemDetailModel details = ItemDetailConverter.getItemDetailsByLanguage(item, lang, defaultLang);

        return ItemDto.builder()
                .id(item.getId())
                .stock(item.getStock())
                .name(details.getName())
                .valueGross(item.getValueGross().asDecimal())
                .producer(ProducerConverter.convertToDto(item.getProducer(), lang, defaultLang))
                .vat(item.getVat().asDouble())
                .image(ImageConverter.convertToDto(item.getImage()))
                .build();
    }

}
