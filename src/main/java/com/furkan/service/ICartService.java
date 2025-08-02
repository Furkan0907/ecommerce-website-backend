package com.furkan.service;

import com.furkan.dto.request.DtoCartIU;
import com.furkan.dto.response.DtoCart;
import com.furkan.dto.response.DtoCartItem;

import java.util.List;

public interface ICartService {

    DtoCart findCartById(Long id);

    List<DtoCart> findAllCarts();

    DtoCart createCart(DtoCartIU cart);

    DtoCart updateCart(Long id, DtoCartIU updatedCart);

    void deleteCart(Long id);

    DtoCart findCartByUserId(Long id);

    DtoCartItem addItemToCart(Long userId, Long productId, Integer quantity);

    DtoCartItem updateItemQuantity(Long userId, Long productId, Integer quantity);

    void removeItemFromCart(Long userId, Long productId);

    void clearCart(Long userId);

    List<DtoCartItem> getCartItems(Long cartId);
}
