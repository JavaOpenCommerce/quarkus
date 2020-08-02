package com.example.business;

import com.example.business.models.AddressModel;
import com.example.business.models.ItemModel;
import com.example.business.models.ProductModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

import static java.math.BigDecimal.ZERO;

@Getter
@EqualsAndHashCode
public final class CardModel {

    private final Map<Long, ProductModel> products;
    private AddressModel deliveryAddress;
    private Payment payment;
    private Value cardValueNett = Value.of(ZERO);
    private Value cardValueGross = Value.of(ZERO);


    public CardModel(Map<Long, ProductModel> products) {
        this.products = products;
        calculateCardValue();
    }

    public void addProductToCard(ItemModel item) {
        Long id = item.getId();
        if (products.containsKey(id)) {
            updateProductAmount(id, products.get(id).getAmount().asInteger() + 1);
        } else {
            ProductModel product = ProductModel.getProduct(item);
            products.put(id, product);
        }
    }

    public void removeProductById(Long id) {
        products.remove(id);
    }

    public void updateProductAmount(Long productId, int amount) {

//        ProductModel product = products.get(productId);
//        int stock = cardService.checkItemStock(productId);
//        if (amount <= stock) {
//           product.setAmount(amount);
//        } else {
//            product.setAmount(stock);
//        }
//        if (amount <= 0) {
//            products.remove(productId);
//        }
//        calculateCardValue();
    }

    public void calculateCardValue() {
        this.cardValueGross = Value.of(products.values()
                .stream()
                .map(p -> p.getValueGross().asDecimal())
                .reduce(ZERO, BigDecimal::add));

        this.cardValueNett = Value.of(products.values()
                .stream()
                .map(p -> p.getValueNett().asDecimal())
                .reduce(ZERO, BigDecimal::add));
    }

//    public void setDeliveryAddress(Long id) {
//        this.deliveryAddress = cardService.getAddressModel(id);
//    }
}
