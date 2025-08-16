package com.furkan.service.impl;

import com.furkan.dto.request.DtoAddressIU;
import com.furkan.dto.response.DtoAddress;
import com.furkan.dto.response.DtoUser;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Address;
import com.furkan.model.User;
import com.furkan.repository.AddressRepository;
import com.furkan.repository.UserRepository;
import com.furkan.service.IAddressService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    private DtoAddress dtoConverter(Address address) {
        DtoAddress dtoAddress = new DtoAddress();
        BeanUtils.copyProperties(address, dtoAddress);

        if (address.getUser() != null) {
            DtoUser dtoUser = new DtoUser();
            BeanUtils.copyProperties(address.getUser(), dtoUser);
            dtoAddress.setUser(dtoUser);
        }

        return dtoAddress;
    }

    @Override
    public DtoAddress saveAddress(DtoAddressIU input) {
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, input.getUserId().toString())));

        Address address = new Address();
        BeanUtils.copyProperties(input, address);
        address.setCreatedAt(new Date());
        address.setUser(user);
        address.setUpdatedAt(new Date());

        Address saved = addressRepository.save(address);
        return dtoConverter(saved);
    }

    @Override
    public DtoAddress findAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ADDRESS_NOT_FOUND, id.toString())));
        return dtoConverter(address);
    }

    @Override
    public List<DtoAddress> findAddressByUserId(Long userId) {
        List<Address> addresses = addressRepository.findAllByUserId(userId);
        if (addresses.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_ADDRESS_FOUND_FOR_THIS_USER, userId.toString()));
        }
        List<DtoAddress> dtoAddresses = new ArrayList<>();
        for (Address address : addresses) {
            dtoAddresses.add(dtoConverter(address));
        }
        return dtoAddresses;
    }

    @Override
    public DtoAddress updateAddress(Long id, DtoAddressIU input) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ADDRESS_NOT_FOUND, id.toString())));

        address.setCity(input.getCity());
        address.setDistrict(input.getDistrict());
        address.setFullAddress(input.getFullAddress());
        address.setPhoneNumber(input.getPhoneNumber());
        address.setUpdatedAt(new Date());

        Address updated = addressRepository.save(address);
        return dtoConverter(updated);
    }

    @Override
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ADDRESS_NOT_FOUND, id.toString())));
        addressRepository.delete(address);
    }
}
