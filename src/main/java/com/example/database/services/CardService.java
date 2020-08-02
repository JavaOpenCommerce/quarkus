package com.example.database.services;

import com.example.business.CardModel;
import com.example.business.models.AddressModel;
import com.example.business.models.ItemModel;
import com.example.business.models.ProductModel;
import com.example.database.entity.Address;
import com.example.database.entity.Item;
import com.example.database.entity.Product;
import com.example.database.repositories.interfaces.AddressRepository;
import com.example.utils.converters.AddressConverter;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class CardService {

    private final ItemService itemService;
    private final AddressRepository addressRepository;

    public CardService(ItemService itemService, AddressRepository addressRepository) {
        this.itemService = itemService;
        this.addressRepository = addressRepository;
    }

    public Uni<ItemModel> getItemModel(Long id) {
        return ofNullable(itemService.getItemById(id)) //TODO
                .orElseThrow(() ->
                        new WebApplicationException("Item with id " + id + " not found", Response.Status.NOT_FOUND));
    }

    private Uni<Map<Long, ProductModel>> getCardProducts(List<Product> products) {
        List<Long> ids = products.stream().map(id -> id.getItemId()).collect(toList());

        return itemService.getItemsListByIdList(ids).onItem().apply(itemModels -> {
            Map<Long, ProductModel> cardProducts = new HashMap<>();
            for (ItemModel im : itemModels) {

                int amount = products.stream()
                        .filter(p -> p.getItemId() == im.getId())
                        .findFirst()
                        .orElse(Product.builder().amount(1).build())
                        .getAmount();

                cardProducts.put(im.getId(), ProductModel.getProduct(im, amount));
            }
            return cardProducts;
        });
    }

    public Uni<CardModel> getCard(List<Product> products) {
        return getCardProducts(products).onItem().apply(map -> new CardModel(map));
    }

    public int checkItemStock(Long id) {
        Item item = ofNullable(new Item()) //TODO
                .orElseThrow(() ->
                        new WebApplicationException("Item with id " + id + " not found", Response.Status.NOT_FOUND));

        if (item.getStock() < 1) {
            //todo handling, issue #6
        }

        return item.getStock();
    }

    public AddressModel getAddressModel(Long id) {
        Address address = ofNullable(new Address()) //TODO
                .orElseThrow(() ->
                        new WebApplicationException("Address with id " + id + " not found", Response.Status.NOT_FOUND));

        return AddressConverter
                .convertToModel(address);
    }

    public List<ItemModel> getShippingMethods() {
        return null; //TODO
//        return itemRepository.getShippingMethodList().stream()
//                .map(i -> ItemConverter.convertToModel(i))
//                .collect(Collectors.toList());

    }
}
