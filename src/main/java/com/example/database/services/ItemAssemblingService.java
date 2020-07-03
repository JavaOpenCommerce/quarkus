package com.example.database.services;

import com.example.database.entity.Category;
import com.example.database.entity.Item;
import com.example.database.entity.ItemDetails;
import com.example.database.entity.Producer;
import com.example.database.repositories.interfaces.CategoryRepository;
import com.example.database.repositories.interfaces.ItemRepository;
import com.example.database.repositories.interfaces.ProducerRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.Set;

@ApplicationScoped
public class ItemAssemblingService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ProducerRepository producerRepository;


    public ItemAssemblingService(ItemRepository itemRepository,
            CategoryRepository categoryRepository,
            ProducerRepository producerRepository) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.producerRepository = producerRepository;
    }

    public Uni<Item> assembleSingleItem(Long id) {
        return Uni.combine().all()
                .unis(itemRepository.getItemById(id),
                        itemRepository.getItemDetailsListByItemId(id),
                        categoryRepository.getCategoriesByItemId(id),
                        producerRepository.getProducerByItemId(id))
                .combinedWith((Item item, Set<ItemDetails> itemDetails, Set<Category> categories, Producer producer) -> {
                    item.setDetails(itemDetails);
                    item.setCategory(categories);
                    item.setProducer(producer);
                    return item;
                });
    }
}
