package com.furkan.service;

import com.furkan.dto.request.DtoCartItemIU;
import com.furkan.dto.response.DtoCartItem;
import com.furkan.model.CartItem;

import java.util.List;

public interface ICartItemService {

    DtoCartItem dtoConverter(CartItem item);

    List<DtoCartItem> dtoListConverter(List<CartItem> itemList);

    DtoCartItem createCartItem(DtoCartItemIU input);

    DtoCartItem findCartItemById(Long id);

    List<DtoCartItem> findAllCartItems();

    DtoCartItem updateCartItem(Long id, DtoCartItemIU input);

    void deleteCartItem(Long id);
}
