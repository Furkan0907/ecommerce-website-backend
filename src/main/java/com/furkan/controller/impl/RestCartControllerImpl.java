package com.furkan.controller.impl;

import com.furkan.controller.IRestCartController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoCartIU;
import com.furkan.dto.response.DtoCart;
import com.furkan.dto.response.DtoCartItem;
import com.furkan.service.ICartService;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class RestCartControllerImpl extends RestBaseController implements IRestCartController {

    @Autowired
    private ICartService cartService;

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessCart(#id)")
    @GetMapping("/{id}")
    @Override
    public RootEntity<DtoCart> findCartById(@PathVariable(value = "id") Long id) {
        return ok(cartService.findCartById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Override
    public RootEntity<List<DtoCart>> findAllCarts() {
        return ok(cartService.findAllCarts());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    @Override
    public RootEntity<DtoCart> createCart(@Valid @RequestBody DtoCartIU cart) {
        return ok(cartService.createCart(cart));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessCart(#id)")
    @PutMapping("/{id}")
    @Override
    public RootEntity<DtoCart> updateCart(@PathVariable(value = "id") Long id, @Valid @RequestBody DtoCartIU updatedCart) {
        return ok(cartService.updateCart(id, updatedCart));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessCart(#id)")
    @DeleteMapping("/{id}")
    @Override
    public RootEntity<Void> deleteCart(@PathVariable(value = "id") Long id) {
        cartService.deleteCart(id);
        return ok();
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @GetMapping("/by-user-id/{id}")
    @Override
    public RootEntity<DtoCart> findCartByUserId(@PathVariable(value = "id") Long id) {
        return ok(cartService.findCartByUserId(id));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessCart(@cartRepository.findByUserId(#userId).orElse(null)?.id)")
    @PostMapping("/{userId}/items")
    @Override
    public RootEntity<DtoCartItem> addItemToCart(@PathVariable(value = "userId") Long userId,
                                                 @RequestParam Long productId,
                                                 @RequestParam(defaultValue = "1") Integer quantity) {
        return ok(cartService.addItemToCart(userId, productId, quantity));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessCart(@cartRepository.findByUserId(#userId).orElse(null)?.id)")
    @PutMapping("/{userId}/items")
    @Override
    public RootEntity<DtoCartItem> updateItemQuantity(@PathVariable(value = "userId") Long userId,
                                                      @RequestParam Long productId,
                                                      @RequestParam Integer quantity) {
        return ok(cartService.updateItemQuantity(userId, productId, quantity));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessCart(@cartRepository.findByUserId(#userId).orElse(null)?.id)")
    @DeleteMapping("/{userId}/items")
    @Override
    public RootEntity<Void> removeItemFromCart(@PathVariable(value = "userId") Long userId,
                                               @RequestParam Long productId) {
        cartService.removeItemFromCart(userId, productId);
        return ok();
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessCart(@cartRepository.findByUserId(#userId).orElse(null)?.id)")
    @DeleteMapping("/{userId}/clear")
    @Override
    public RootEntity<Void> clearCart(@PathVariable(value = "userId") Long userId) {
        cartService.clearCart(userId);
        return ok();
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.canAccessCart(#cartId)")
    @GetMapping("/{cartId}/items")
    @Override
    public RootEntity<List<DtoCartItem>> getCartItems(@PathVariable(value = "cartId") Long cartId) {
        return ok(cartService.getCartItems(cartId));
    }
}
