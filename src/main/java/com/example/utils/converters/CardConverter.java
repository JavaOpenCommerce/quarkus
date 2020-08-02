package com.example.utils.converters;

import com.example.business.CardModel;
import com.example.rest.dtos.CardDto;
import com.example.rest.dtos.ProductDto;

import java.util.HashMap;
import java.util.Map;

public interface CardConverter {

    static CardDto convertToDto(CardModel cardModel, String lang, String defaultLang)  {
        Map<Long, ProductDto> productDtos = new HashMap<>();

        cardModel.getProducts().values()
                .forEach(p -> productDtos.put(p.getItemModel().getId(), ProductConverter.convertToDto(p, lang, defaultLang)));

        return CardDto.builder()
                .cardValueGross(cardModel.getCardValueGross().asDecimal())
                .cardValueNett(cardModel.getCardValueNett().asDecimal())
                .deliveryAddress(null)
                .products(productDtos)
                .build();
    }
}
