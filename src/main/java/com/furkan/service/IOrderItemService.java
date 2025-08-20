package com.furkan.service;

import com.furkan.dto.request.DtoOrderItemIU;
import com.furkan.dto.response.DtoOrderItem;
import com.furkan.model.Order;
import com.furkan.model.OrderItem;

import java.util.List;

public interface IOrderItemService {

    DtoOrderItem createOrderItem(DtoOrderItemIU input);

    DtoOrderItem findOrderItemById(Long id);

    List<DtoOrderItem> findAllByOrderId(Long orderId);

    DtoOrderItem updateOrderItem(Long id, DtoOrderItemIU input);

    void deleteOrderItem(Long id);

    DtoOrderItem dtoConverter(OrderItem orderItem);

    DtoOrderItem cancelOrderItem(Long id);

    OrderItem refundOrderItem(Long id);

    DtoOrderItem markOrderItemShipped(Long id);

    DtoOrderItem deliverOrderItem(Long id);

    void updateOrderAggregateStatus(Order order);
}
