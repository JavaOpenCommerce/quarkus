package com.example.rest.services;

import com.example.database.services.SearchService;
import com.example.rest.dtos.ItemDto;
import com.example.rest.dtos.PageDto;
import com.example.utils.converters.ItemPageConverter;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchDtoService {

    private final SearchService searchService;

    public SearchDtoService(SearchService searchService) {this.searchService = searchService;}


    public PageDto<ItemDto> searchForAProduct(String pattern, int pageSize, int pageIndex) {
        return ItemPageConverter
                .convertToDto(searchService.searchForAProduct(pattern, pageSize, pageIndex));
    }
}
