package com.furkan.controller;

import com.furkan.dto.request.DtoOrderItemIU;
import com.furkan.dto.response.DtoOrderItem;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestOrderItemController {

    RootEntity<DtoOrderItem> createOrderItem(DtoOrderItemIU input);

    RootEntity<DtoOrderItem> findOrderItemById(Long id);

    RootEntity<List<DtoOrderItem>> findAllByOrderId(Long orderId);

    RootEntity<DtoOrderItem> updateOrderItem(Long id, DtoOrderItemIU input);

    RootEntity<Void> deleteOrderItem(Long id);
}
