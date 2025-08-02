package com.furkan.controller;

import com.furkan.dto.request.DtoCartItemIU;
import com.furkan.dto.response.DtoCartItem;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestCartItemController {

    RootEntity<DtoCartItem> createCartItem(DtoCartItemIU input);

    RootEntity<DtoCartItem> findCartItemById(Long id);

    RootEntity<List<DtoCartItem>> findAllCartItems();

    RootEntity<DtoCartItem> updateCartItem(Long id, DtoCartItemIU input);

    RootEntity<Void> deleteCartItem(Long id);
}
