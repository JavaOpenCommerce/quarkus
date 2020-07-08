package com.example.rest.services;

import com.example.database.services.StoreService;
import com.example.elasticsearch.SearchRequest;
import com.example.rest.dtos.CategoryDto;
import com.example.rest.dtos.ItemDetailDto;
import com.example.rest.dtos.ItemDto;
import com.example.rest.dtos.PageDto;
import com.example.rest.dtos.ProducerDto;
import com.example.utils.LanguageResolver;
import com.example.utils.converters.CategoryConverter;
import com.example.utils.converters.ItemDetailConverter;
import com.example.utils.converters.ItemPageConverter;
import com.example.utils.converters.ProducerConverter;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class StoreDtoService {

    private final StoreService storeService;
    private final LanguageResolver langRes;

    public StoreDtoService(StoreService storeService, LanguageResolver langRes) {
        this.storeService = storeService;
        this.langRes = langRes;
    }

    public Uni<ItemDetailDto> getItemById(Long id) {
        return storeService
                .getItemById(id)
                .onItem()
                .apply(i -> ItemDetailConverter.convertToDto(i, langRes.getLanguage(), langRes.getDefault()));
    }

    public Uni<PageDto<ItemDto>> getFilteredItems(SearchRequest request) {

        return storeService.getFilteredItemsPage(request).onItem()
                .apply(itemPage -> ItemPageConverter.convertToDto(itemPage, langRes.getLanguage(), langRes.getDefault()));
    }

    public Uni<List<CategoryDto>> getAllCategories() {
        return storeService.getAllCategories().onItem().apply(categoryModels ->
                categoryModels.stream()
                        .map(cat -> CategoryConverter.convertToDto(cat, langRes.getLanguage(), langRes.getDefault()))
                        .collect(toList()));
    }

    public Uni<List<ProducerDto>> getAllProducers() {
        return storeService.getAllProducers().onItem().apply(producerModels ->
                producerModels.stream()
                        .map(prod -> ProducerConverter.convertToDto(prod, langRes.getLanguage(), langRes.getDefault()))
                        .collect(toList()));
    }
}
