package com.example.database.services;

import com.example.business.models.CategoryModel;
import com.example.business.models.ItemModel;
import com.example.business.models.PageModel;
import com.example.business.models.ProducerModel;
import com.example.database.entity.Category;
import com.example.database.entity.Item;
import com.example.database.repositories.CategoryRepository;
import com.example.database.repositories.ItemRepository;
import com.example.database.repositories.ProducerRepository;
import com.example.utils.LanguageResolver;
import com.example.utils.converters.CategoryConverter;
import com.example.utils.converters.ItemConverter;
import com.example.utils.converters.ProducerConverter;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class StoreService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ProducerRepository producerRepository;
    private final LanguageResolver languageResolver;


    public StoreService(CategoryRepository categoryRepository,
            ItemRepository itemRepository,
            ProducerRepository producerRepository, LanguageResolver languageResolver) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.producerRepository = producerRepository;
        this.languageResolver = languageResolver;
    }

    public List<CategoryModel> getCategoryList() {
        return categoryRepository.findAll().stream()
                .filter(cm -> !"Shipping".equalsIgnoreCase(cm.getCategoryName()))
                .map(c -> CategoryConverter.convertToModel(c))
                .collect(Collectors.toList());
    }

    public List<ProducerModel> getProducerList() {
        return producerRepository.findAll().stream()
                .map(p -> ProducerConverter.convertToModel(p))
                .collect(Collectors.toList());
    }

    public ItemModel getItemById(Long id) {
        Item item = itemRepository.findByIdOptional(id)
                .orElseThrow(() ->
                        new WebApplicationException("Item with id " + id + " not found", Response.Status.NOT_FOUND));
        return ItemConverter
                .convertToModel(item);
    }

    public PageModel<ItemModel> getPageOfAllItems(int pageIndex, int pageSize) {
        PanacheQuery<Item> page = itemRepository.getAll(pageIndex, pageSize);
        return getItemModelPage(pageIndex, pageSize, page);
    }

    public PageModel<ItemModel> getItemsPageByCategory(Long categoryId, int pageIndex, int pageSize) {
        PanacheQuery<Item> itemPanacheQuery = itemRepository
                .listItemByCategoryId(categoryId, pageIndex, pageSize);

        return getItemModelPage(pageIndex, pageSize, itemPanacheQuery);
    }

    public PageModel<ItemModel> getItemsPageByProducer(Long producerId, int pageIndex, int pageSize) {
        PanacheQuery<Item> itemPanacheQuery = itemRepository
                .listItemByProducerId(producerId, pageIndex, pageSize);

        return getItemModelPage(pageIndex, pageSize, itemPanacheQuery);
    }

    private PageModel<ItemModel> getItemModelPage(int pageIndex, int pageSize, PanacheQuery<Item> itemPanacheQuery) {
        List<ItemModel> itemModels = itemPanacheQuery.list().stream()
                .filter(i -> validUserProductCategory(i.getCategory()))
                .map(i -> ItemConverter.convertToModel(i))
                .collect(Collectors.toList());

        return PageModel.<ItemModel>builder()
                .pageCount(itemPanacheQuery.pageCount())
                .pageNumber(pageIndex)
                .pageSize(pageSize)
                .totalElementsCount((int) itemPanacheQuery.count())
                .items(itemModels)
                .build();
    }

    private boolean validUserProductCategory(Set<Category> categories) {
        for (Category cat : categories) {
            if ("Shipping".equalsIgnoreCase(cat.getCategoryName())) {
                return false;
            }
        }
        return true;
    }
}