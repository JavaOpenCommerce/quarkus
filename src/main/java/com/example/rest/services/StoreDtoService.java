package com.example.rest.services;

import com.example.database.services.StoreService;
import com.example.elasticsearch.SearchRequest;
import com.example.rest.dtos.ItemDetailDto;
import com.example.rest.dtos.ItemDto;
import com.example.utils.LanguageResolver;
import com.example.utils.converters.ItemConverter;
import com.example.utils.converters.ItemDetailConverter;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class StoreDtoService {

    private final StoreService storeService;
    private final LanguageResolver langResolver;

    public StoreDtoService(StoreService storeService, LanguageResolver langResolver) {
        this.storeService = storeService;
        this.langResolver = langResolver;
    }

    public Uni<ItemDetailDto> getItemById(Long id) {
        return storeService
                .getItemById(id)
                .onItem()
                .apply(i -> ItemDetailConverter.convertToDto(i, langResolver.getLanguage(), langResolver.getDefault()));
    }

    public Uni<List<ItemDto>> getFilteredItems(SearchRequest request) {
        return storeService.getFilteredItems(request).onItem()
                .apply(items -> items.stream()
                        .map(it -> ItemConverter.convertToDto(it, langResolver.getLanguage(), langResolver.getDefault()))
                        .collect(toList()));
    }
}
