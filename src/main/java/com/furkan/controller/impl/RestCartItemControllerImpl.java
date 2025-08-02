package com.furkan.controller.impl;

import com.furkan.controller.IRestCartItemController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoCartItemIU;
import com.furkan.dto.response.DtoCartItem;
import com.furkan.service.ICartItemService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class RestCartItemControllerImpl extends RestBaseController implements IRestCartItemController {

    @Autowired
    private ICartItemService cartItemService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    @Override
    public RootEntity<DtoCartItem> createCartItem(@Valid @RequestBody DtoCartItemIU input) {
        return ok(cartItemService.createCartItem(input));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Override
    public RootEntity<DtoCartItem> findCartItemById(@PathVariable(value = "id") Long id) {
        return ok(cartItemService.findCartItemById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Override
    public RootEntity<List<DtoCartItem>> findAllCartItems() {
        return ok(cartItemService.findAllCartItems());
    }

    @PreAuthorize("hasRole('ADMIN'))")
    @PutMapping("/{id}")
    @Override
    public RootEntity<DtoCartItem> updateCartItem(@PathVariable(value = "id") Long id, @Valid @RequestBody DtoCartItemIU input) {
        return ok(cartItemService.updateCartItem(id, input));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Override
    public RootEntity<Void> deleteCartItem(@PathVariable(value = "id") Long id) {
        cartItemService.deleteCartItem(id);
        return ok();
    }
}
