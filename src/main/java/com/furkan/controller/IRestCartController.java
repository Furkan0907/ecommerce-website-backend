package com.furkan.controller;

import com.furkan.dto.request.DtoCartIU;
import com.furkan.dto.response.DtoCart;
import com.furkan.dto.response.DtoCartItem;
import com.furkan.dto.response.DtoOrder;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestCartController {

    RootEntity<DtoCart> findCartById(Long id);

    RootEntity<List<DtoCart>> findAllCarts();

    RootEntity<DtoCart> createCart(DtoCartIU cart);

    RootEntity<DtoCart> updateCart(Long id, DtoCartIU updatedCart);

    RootEntity<Void> deleteCart(Long id);

    RootEntity<DtoCart> findCartByUserId(Long id);

    RootEntity<DtoCartItem> addItemToCart(Long userId, Long productId, Integer quantity);

    RootEntity<DtoCartItem> updateItemQuantity(Long userId, Long productId, Integer quantity);

    RootEntity<Void> removeItemFromCart(Long userId, Long productId);

    RootEntity<Void> clearCart(Long userId);

    RootEntity<List<DtoCartItem>> getCartItems(Long userId);

    RootEntity<DtoOrder> confirmCart(Long userId, Long addressId);
}
