package com.furkan.controller;

import com.furkan.dto.request.DtoAddressIU;
import com.furkan.dto.response.DtoAddress;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestAddressController {

    RootEntity<DtoAddress> saveAddress(DtoAddressIU input);

    RootEntity<DtoAddress> findAddressById(Long id);

    RootEntity<List<DtoAddress>> findAddressByUserId(Long userId);

    RootEntity<DtoAddress> updateAddress(Long id, DtoAddressIU input);

    RootEntity<Void> deleteAddress(Long id);
}
