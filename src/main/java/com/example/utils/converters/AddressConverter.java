package com.example.utils.converters;

import com.example.business.models.AddressModel;
import com.example.database.entity.Address;
import com.example.rest.dtos.AddressDto;


public interface AddressConverter {

    static AddressModel convertToModel(Address address) {
        return AddressModel.builder()
                .id(address.getId())
                .street(address.getStreet())
                .local(address.getLocal())
                .city(address.getCity())
                .zip(address.getZip())
                .build();
    }

    static Address convertToEntity(AddressModel addressModel) {
        return Address.builder()
                .id(addressModel.getId())
                .userId(addressModel.getUser_id())
                .street(addressModel.getStreet())
                .city(addressModel.getCity())
                .local(addressModel.getLocal())
                .zip(addressModel.getZip())
                .build();
    }

    static AddressDto convertToDto(AddressModel address) {
        return AddressDto.builder()
                .id(address.getId())
                .street(address.getStreet())
                .local(address.getLocal())
                .city(address.getCity())
                .zip(address.getZip())
                .build();
    }
}
