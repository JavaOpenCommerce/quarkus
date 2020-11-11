package com.example.utils.converters;

import com.example.business.models.OrderDetailsModel;
import com.example.business.models.ProductModel;
import com.example.database.entity.*;

import java.util.Map;

public interface OrderDetailsConverter {

    static OrderDetailsModel convertToModel(OrderDetails orderDetails, Map<Long, ProductModel> products, Address address, UserEntity userEntity) {

        return OrderDetailsModel.builder()
                .id(orderDetails.getId())
                .creationDate(orderDetails.getCreationDate())
                .orderStatus(orderDetails.getOrderStatus().toString())
                .paymentMethod(orderDetails.getPaymentMethod().toString())
                .paymentStatus(orderDetails.getPaymentStatus().toString())
                .address(AddressConverter.convertToModel(address))
                //.user(UserConverter.convertToModel(userEntity))
                .products(products)
                .build();
    }

    //Requires OrderDetailsModel
    static OrderDetails convertToEntity(OrderDetailsModel orderDetailsModel) {

        return OrderDetails.builder()
                .creationDate(orderDetailsModel.getCreationDate())
                .shippingAddressId(orderDetailsModel.getAddress().getId())
                .orderStatus(OrderStatus.valueOf(orderDetailsModel.getOrderStatus()))
                .paymentMethod(PaymentMethod.valueOf(orderDetailsModel.getPaymentMethod()))
                .paymentStatus(PaymentStatus.valueOf(orderDetailsModel.getPaymentMethod()))
                .userEntityId(orderDetailsModel.getUser().getId())
                .build();
    }
}
