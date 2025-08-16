package com.furkan.service;

import com.furkan.dto.request.DtoAddressIU;
import com.furkan.dto.response.DtoAddress;

import java.util.List;

public interface IAddressService {

    DtoAddress saveAddress(DtoAddressIU input);

    DtoAddress findAddressById(Long id);

    List<DtoAddress> findAddressByUserId(Long userId);

    DtoAddress updateAddress(Long id, DtoAddressIU input);

    void deleteAddress(Long id);
}
