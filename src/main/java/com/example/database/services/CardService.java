package com.example.database.services;

import com.example.business.models.AddressModel;
import com.example.business.models.ItemModel;
import com.example.database.entity.Address;
import com.example.database.entity.Item;
import com.example.database.repositories.AddressRepository;
import com.example.database.repositories.ItemRepository;
import com.example.utils.LanguageResolver;
import com.example.utils.converters.AddressConverter;
import com.example.utils.converters.ItemConverter;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class CardService {

    private final ItemRepository itemRepository;
    private final AddressRepository addressRepository;
    private final LanguageResolver languageResolver;

    public CardService(ItemRepository itemRepository,
            AddressRepository addressRepository, LanguageResolver languageResolver) {
        this.itemRepository = itemRepository;
        this.addressRepository = addressRepository;
        this.languageResolver = languageResolver;
    }

    public ItemModel getItemModel(Long id) {
        Item item = itemRepository.findByIdOptional(id)
                .orElseThrow(() ->
                        new WebApplicationException("Item with id " + id + " not found", Response.Status.NOT_FOUND));

        return ItemConverter
                .convertToModel(item, languageResolver.getLanguage(), languageResolver.getDefault());
    }

    public int checkItemStock(Long id) {
        Item item = itemRepository.findByIdOptional(id)
                .orElseThrow(() ->
                        new WebApplicationException("Item with id " + id + " not found", Response.Status.NOT_FOUND));

        return item.getStock();
    }

    public AddressModel getAddressModel(Long id) {
        Address address = addressRepository.findByIdOptional(id)
                .orElseThrow(() ->
                        new WebApplicationException("Address with id " + id + " not found", Response.Status.NOT_FOUND));

        return AddressConverter
                .convertToModel(address);
    }
}
