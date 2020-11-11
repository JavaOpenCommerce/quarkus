package com.example.database.services;

import com.example.business.models.ItemModel;
import com.example.business.models.OrderDetailsModel;
import com.example.business.models.ProductModel;
import com.example.database.entity.Address;
import com.example.database.entity.ItemQuantity;
import com.example.database.entity.OrderDetails;
import com.example.database.entity.UserEntity;
import com.example.database.repositories.interfaces.AddressRepository;
import com.example.database.repositories.interfaces.ItemQuantityRepository;
import com.example.database.repositories.interfaces.OrderDetailsRepository;
import com.example.database.repositories.interfaces.UserRepository;
import com.example.utils.converters.OrderDetailsConverter;
import com.example.utils.converters.ProductConverter;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.smallrye.mutiny.Uni.combine;
import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class OrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final ItemQuantityRepository itemQuantityRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository, ItemQuantityRepository itemQuantityRepository, AddressRepository addressRepository, UserRepository userRepository, ItemService itemService) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.itemQuantityRepository = itemQuantityRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }

    public Uni<OrderDetailsModel> getOrderDetailsById(Long id) {
        Uni<OrderDetails> orderDetailsUni = orderDetailsRepository.findOrderDetailsById(id);
        Uni<Map<Long, ProductModel>> itemQuantityListUni = itemQuantityRepository.getItemQuantitiesByOrderId(id)
                .onItem().produceUni(this::getProducts);

        //TODO - how to pass address_id & user_id
        Uni<Address> addressUni = addressRepository.findById(1L);
        Uni<UserEntity> userUni = userRepository.findById(1L);

        return combine().all().unis(orderDetailsUni, itemQuantityListUni, addressUni, userUni)
                .combinedWith(OrderDetailsConverter::convertToModel);
    }

    @Transactional
    public Uni<OrderDetailsModel> saveOrderDetails(Uni<OrderDetailsModel> orderDetailsModel) {
        Uni<OrderDetails> savedOrderDetails = orderDetailsModel.onItem()
                .produceUni(od -> orderDetailsRepository.saveOrder(
                        OrderDetailsConverter.convertToEntity(od))
                );

        Uni<List<ItemQuantity>> itemQuantities = combine().all().unis(orderDetailsModel, savedOrderDetails).combinedWith(
                (OrderDetailsModel odm, OrderDetails sod) ->
                    odm.getProducts().values().stream()
                            .map(productModel -> ProductConverter.convertToItemQuantity(productModel, sod.getId()))
                            .collect(toList())
        );

        //TODO - refactor & how to return Model again
        Uni<List<ItemQuantity>> savedItemQuantities = itemQuantities.onItem()
                .apply(list -> {
                    List<Uni<ItemQuantity>> savedQuantities = list.stream()
                            .map(itemQuantityRepository::saveItemQuantity)
                            .collect(toList());
                    return (List<ItemQuantity>) combine().all().unis(savedQuantities);
                });

//        return combine()
//                .all().unis(
//                        savedOrderDetails,
//                        savedItemQuantities,
//
//                        orderDetailsModel.onItem().apply(OrderDetailsModel::getUser))
//                .combinedWith(OrderDetailsConverter::convertToModel);

        return Uni.createFrom().nullItem();

    }

    private Uni<Map<Long,ProductModel>> getProducts(List<ItemQuantity> products) {
        List<Long> ids = products.stream()
                .map(ItemQuantity::getItemId)
                .collect(toList());

        return itemService.getItemsListByIdList(ids).onItem().apply(itemModels -> {
            Map<Long, ProductModel> cardProducts = new HashMap<>();
            for (ItemModel im : itemModels) {

                int amount = products.stream()
                        .filter(p -> p.getItemId() == im.getId())
                        .findFirst()
                        .orElse(ItemQuantity.builder().amount(1).build())
                        .getAmount();

                cardProducts.put(im.getId(), ProductModel.getProduct(im, amount));
            }
            return cardProducts;
        });
    }
}
