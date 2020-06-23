package com.example.utils.converters;

import com.example.business.Product;
import com.example.rest.dtos.ProductDto;

public interface ProductConverter {

    static ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .item(ItemConverter.convertToDto(product.getItemModel()))
                .amount(product.getAmount().asInteger())
                .valueGross(product.getValueGross().asDecimal())
                .valueNett(product.getValueNett().asDecimal())
                .build();
    }
}
