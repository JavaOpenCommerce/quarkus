package com.example.database.repositories.interfaces;

import com.example.database.entity.Item;
import com.example.database.entity.ItemDetails;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface ItemRepository {

    Uni<List<Item>> getAllItems();

    Uni<Item> getItemById(Long id);

    Uni<List<ItemDetails>> getAllItemDetails();

    Uni<List<ItemDetails>> getItemDetailsListByItemId(Long id);

    List<Item> listItemByCategoryId(Long categoryId, int pageIndex, int pageSize);

    List<Item> listItemByProducerId(Long producerId, int pageIndex, int pageSize);

    List<Item> getShippingMethodList();



}
