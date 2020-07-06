package com.example.database.services;

import com.example.business.models.ItemModel;
import com.example.database.entity.Category;
import com.example.database.entity.Item;
import com.example.database.entity.ItemDetails;
import com.example.database.entity.Producer;
import com.example.database.repositories.interfaces.CategoryRepository;
import com.example.database.repositories.interfaces.ItemRepository;
import com.example.database.repositories.interfaces.ProducerRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

import static com.example.utils.converters.ItemConverter.convertToModel;
import static io.smallrye.mutiny.Uni.combine;
import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class ItemAssemblingService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ProducerRepository producerRepository;


    public ItemAssemblingService(ItemRepository itemRepository,
            CategoryRepository categoryRepository, ProducerRepository producerRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.producerRepository = producerRepository;
    }

    public Uni<ItemModel> getSingleItem(Long id) {
        Uni<Item> itemUni = itemRepository.getItemById(id);
        Uni<List<ItemDetails>> itemDetailsUni = itemRepository.getItemDetailsListByItemId(id);
        Uni<List<Category>> categoriesUni = categoryRepository.getCategoriesByItemId(id);
        Uni<Producer> producerUni = producerRepository.getProducerByItemId(id);

        return combine().all()
                .unis(itemUni, itemDetailsUni, categoriesUni, producerUni)
                .combinedWith(
                        (item, itemDetails, categories, producer) ->
                                convertToModel(item, itemDetails, categories, producer));
    }

    public Uni<List<ItemModel>> getFullItemList() {
        Uni<List<Item>> itemsUni = itemRepository.getAllItems();
        Uni<List<Category>> categoriesUni = categoryRepository.getAll();
        Uni<List<ItemDetails>> itemDetailsUni = itemRepository.getAllItemDetails();
        Uni<List<Producer>> producersUni = producerRepository.getAll();

        return combine().all()
                .unis(itemsUni, itemDetailsUni, categoriesUni, producersUni)
                .combinedWith(
                        (items, itemDetails, categories, producers) ->
                                assembleItemModelList(items, itemDetails, categories, producers));
    }

    private List<ItemModel> assembleItemModelList(List<Item> items,
            List<ItemDetails> itemDetails,
            List<Category> categories,
            List<Producer> producers) {

        List<ItemModel> itemModels = new ArrayList<>();
        for (Item item : items) {
            List<Category> categoriesFiltered = categories.stream()
                    .filter(c -> item.getCategoryIds().contains(c.getId()))
                    .collect(toList());

            List<ItemDetails> itemDetailsFiltered = itemDetails.stream()
                    .filter(id -> id.getItemId() == item.getId())
                    .collect(toList());

            Producer producerRetrieved = producers.stream()
                    .filter(p -> p.getId() == item.getProducerId())
                    .findAny()
                    .orElse(Producer.builder().build());

            itemModels
                    .add(convertToModel(item, itemDetailsFiltered, categoriesFiltered, producerRetrieved));
        }
        return itemModels;
    }
}
