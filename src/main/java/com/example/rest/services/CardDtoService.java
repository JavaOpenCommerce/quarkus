package com.example.rest.services;

import com.example.database.services.CardService;
import com.example.rest.dtos.ItemDto;
import com.example.utils.converters.ItemConverter;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CardDtoService {

    private final CardService cardService;

    public CardDtoService(CardService cardService) {this.cardService = cardService;}


    public List<ItemDto> getShippingMethods() {
        return cardService.getShippingMethods().stream()
                .map(i -> ItemConverter.convertToDto(i))
                .collect(Collectors.toList());
    }
}
