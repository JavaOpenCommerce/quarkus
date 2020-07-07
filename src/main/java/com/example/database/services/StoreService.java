package com.example.database.services;

import com.example.business.models.ItemModel;
import com.example.elasticsearch.SearchRequest;
import com.example.elasticsearch.SearchService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import lombok.extern.jbosslog.JBossLog;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import static java.lang.Long.parseLong;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@JBossLog
@ApplicationScoped
public class StoreService {

    private final ItemService itemService;
    private final SearchService searchService;


    public StoreService(ItemService itemService, SearchService searchService) {
        this.itemService = itemService;
        this.searchService = searchService;
    }

    public Uni<ItemModel> getItemById(Long id) {
        return itemService.getItemById(id);
    }

    public Uni<List<ItemModel>> getFilteredItems(SearchRequest request) {

        Uni<List<Long>> filteredItemIds = searchService
                .searchItemsBySearchRequest(request).onItem().apply(json -> {
                    if (json == null || json.isEmpty() || json.getJsonObject("hits") == null) {
                        return emptyList();
                    }
                    JsonArray hits = json
                            .getJsonObject("hits")
                            .getJsonArray("hits");

                    return hits.stream()
                            .map(id -> parseLong(id.toString().split("\"")[3]))
                            .collect(toList());
                });

        return itemService.getItemsListByIdList(filteredItemIds.await().indefinitely());
    }
}


//    private boolean validUserCategory(List<Category> categories) {
//        return categories.stream()
//                .flatMap(category -> category.getDetails().stream())
//                .allMatch(details -> !"shipping".equalsIgnoreCase(details.getName()));
//    }


//    private PageModel<ItemModel> getItemModelPage(int pageIndex, int pageSize, List<Item> itemPanacheQuery) {
//
//        return null; //todo
//        List<ItemModel> itemModels = itemPanacheQuery.stream() //TODO
//                //.filter(i -> validUserCategory(i.getCategory())) //TODO
//                .map(i -> ItemConverter.convertToModel(i))
//                .collect(Collectors.toList());
//
//        return PageModel.<ItemModel>builder()
//                .pageCount(0) //TODO
//                .pageNumber(pageIndex)
//                .pageSize(pageSize)
//                .totalElementsCount(0) //TODO
//                .items(itemModels)
//                .build();
//    }
