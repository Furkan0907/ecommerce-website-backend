package com.furkan.controller.impl;

import com.furkan.controller.IRestAddressController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoAddressIU;
import com.furkan.dto.response.DtoAddress;
import com.furkan.service.IAddressService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class RestAddressControllerImpl extends RestBaseController implements IRestAddressController {

    @Autowired
    private IAddressService addressService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    @Override
    public RootEntity<DtoAddress> saveAddress(@Valid @RequestBody DtoAddressIU input) {
        return ok(addressService.saveAddress(input));
    }

    @PreAuthorize("@securityService.isOwnerOrAdmin(#userId)")
    @GetMapping("/user/{userId}")
    @Override
    public RootEntity<List<DtoAddress>> findAddressByUserId(@PathVariable Long userId) {
        return ok(addressService.findAddressByUserId(userId));
    }

    @PreAuthorize("@securityService.isOwnerOrAdmin(#input.userId)")
    @PutMapping("/{id}")
    @Override
    public RootEntity<DtoAddress> updateAddress(@PathVariable Long id, @Valid @RequestBody DtoAddressIU input) {
        return ok(addressService.updateAddress(id, input));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isOwner(#id)")
    @DeleteMapping("/{id}")
    @Override
    public RootEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ok();
    }
}
