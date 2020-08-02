package com.example.rest.services;

import com.example.database.entity.Product;
import com.example.database.services.CardService;
import com.example.rest.dtos.CardDto;
import com.example.rest.dtos.ItemDto;
import com.example.utils.LanguageResolver;
import com.example.utils.converters.CardConverter;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CardDtoService {

    private final CardService cardService;
    private final LanguageResolver langResolver;

    public CardDtoService(CardService cardService, LanguageResolver langResolver) {
        this.cardService = cardService;
        this.langResolver = langResolver;
    }

    public List<ItemDto> getShippingMethods() {
        return null;
//        cardService.getShippingMethods().stream()
//                .map(i -> ItemConverter.convertToDto(i, langResolver.getLanguage(), langResolver.getDefault()))
//                .collect(Collectors.toList());
    }

    public Uni<CardDto> getCard(List<Product> products, RoutingContext routingContext) {
        return cardService
                .getCard(products)
                .onItem()
                .apply(cardModel -> CardConverter
                        .convertToDto(cardModel, langResolver.getLanguage(routingContext), langResolver.getDefault()));
    }
}
