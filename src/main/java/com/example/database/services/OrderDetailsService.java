package com.example.database.services;

import com.example.business.models.ItemModel;
import com.example.business.models.OrderDetailsModel;
import com.example.business.models.ProductModel;
import com.example.database.entity.Address;
import com.example.database.entity.CardProduct;
import com.example.database.entity.OrderDetails;
import com.example.database.entity.UserEntity;
import com.example.database.repositories.interfaces.AddressRepository;
import com.example.database.repositories.interfaces.OrderDetailsRepository;
import com.example.database.repositories.interfaces.UserRepository;
import com.example.utils.converters.AddressConverter;
import com.example.utils.converters.OrderDetailsConverter;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

import static com.example.utils.converters.JsonConverter.convertToObject;
import static io.smallrye.mutiny.Uni.combine;
import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class OrderDetailsService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository, AddressRepository addressRepository,
                               UserRepository userRepository, ItemService itemService) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.itemService = itemService;
    }


    public Uni<OrderDetailsModel> getOrderDetailsById(Long id) {
        Uni<OrderDetails> orderDetailsUni = orderDetailsRepository.findOrderDetailsById(id);

        Uni<Map<Long, ProductModel>> itemQuantityListUni = orderDetailsUni.onItem()
                .transformToUni(od -> getProducts(od.getProductsJson()));

        Uni<Address> addressUni = orderDetailsUni.onItem()
                .transformToUni(od -> addressRepository.findById(od.getId()));

        Uni<UserEntity> userUni = orderDetailsUni.onItem()
                .transformToUni(od -> userRepository.findById(od.getId()));

        return combine().all().unis(orderDetailsUni, itemQuantityListUni, addressUni, userUni)
                .combinedWith(OrderDetailsConverter::convertToModel);
    }

    public Uni<OrderDetailsModel> saveOrderDetails(Uni<OrderDetailsModel> orderDetailsModel) {
        Uni<OrderDetails> savedOrderDetails = orderDetailsModel.onItem()
                .transformToUni(od -> {
                    System.out.println("Inside save");
                    return orderDetailsRepository.saveOrder(
                        OrderDetailsConverter.convertToEntity(od));
                }
        );
        Uni<Map<Long, ProductModel>> productsMapUni = savedOrderDetails.onItem()
                .transformToUni(sod -> getProducts(sod.getProductsJson()));

        return combine().all().unis(
                savedOrderDetails,
                productsMapUni,
                orderDetailsModel.onItem().transform(odm -> AddressConverter.convertToEntity(odm.getAddress())),
                Uni.createFrom().item(UserEntity.builder().id(1L).build())) //TODO
                .combinedWith(OrderDetailsConverter::convertToModel);
    }

    private Uni<Map<Long, ProductModel>> getProducts(String productsJson) {

        List<CardProduct> cardProducts =
                convertToObject(productsJson, new ArrayList<CardProduct>() {}.getClass().getGenericSuperclass());

        List<Long> ids = getProductIdList(cardProducts);

        return itemService.getItemsListByIdList(ids).onItem().transform(itemModels -> {
            Map<Long, ProductModel> cardProductsMap = new HashMap<>();
            for (ItemModel im : itemModels) {
                int amount = cardProducts.stream()
                        .filter(Objects::nonNull)
                        .filter(p -> p.getItemId().equals(im.getId()))
                        .findFirst()
                        .orElse(CardProduct.builder().amount(1).build())
                        .getAmount();
                cardProductsMap.put(im.getId(), ProductModel.getProduct(im, amount));
            }
            return cardProductsMap;
        });
    }

    private List<Long> getProductIdList(List<CardProduct> cardProducts) {
        return cardProducts.stream()
                .filter(Objects::nonNull)
                .map(CardProduct::getItemId)
                .collect(toList());
    }
}
