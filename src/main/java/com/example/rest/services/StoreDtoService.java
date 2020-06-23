package com.example.rest.services;

import com.example.database.services.StoreService;
import com.example.rest.dtos.CategoryDto;
import com.example.rest.dtos.ItemDetailDto;
import com.example.rest.dtos.ItemDto;
import com.example.rest.dtos.PageDto;
import com.example.rest.dtos.ProducerDto;
import com.example.utils.converters.CategoryConverter;
import com.example.utils.converters.ItemDetailConverter;
import com.example.utils.converters.ItemPageConverter;
import com.example.utils.converters.ProducerConverter;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class StoreDtoService {

    private final StoreService storeService;

    public StoreDtoService(StoreService storeService) {this.storeService = storeService;}


    public ItemDetailDto getItemById(Long id) {
        return ItemDetailConverter.convertToDto(storeService.getItemDetailModel(id));
    }

    public PageDto<ItemDto> getPageOfAllItems(int pageIndex, int pageSize) {
        return ItemPageConverter
                .convertToDto(storeService.getPageOfAllItems(pageIndex, pageSize));
    }

    public PageDto<ItemDto> getItemsPageByCategory(Long categoryId, int pageIndex, int pageSize) {
        return ItemPageConverter
                .convertToDto(storeService.getItemsPageByCategory(categoryId, pageIndex, pageSize));
    }

    public PageDto<ItemDto> getItemsPageByProducer(Long producerId, int pageIndex, int pageSize) {
        return ItemPageConverter
                .convertToDto(storeService.getItemsPageByProducer(producerId, pageIndex, pageSize));
    }

    public List<CategoryDto> getCategoryList() {
        return storeService.getCategoryList().stream()
                .map(c -> CategoryConverter.convertToDto(c))
                .collect(Collectors.toList());
    }

    public List<ProducerDto> getProducerList() {
        return storeService.getProducerList().stream()
                .map(c -> ProducerConverter.convertToDto(c))
                .collect(Collectors.toList());
    }
}
