package com.furkan.controller;

import com.furkan.dto.request.DtoOrderIU;
import com.furkan.dto.response.DtoOrder;
import com.furkan.enums.OrderStatus;
import com.furkan.utils.RestPageableEntity;
import com.furkan.utils.RestPageableRequest;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestOrderController {

    RootEntity<DtoOrder> createOrder(DtoOrderIU input);

    RootEntity<DtoOrder> findOrderById(Long id);

    RootEntity<RestPageableEntity<DtoOrder>> findAllOrders(RestPageableRequest pageableRequest);

    RootEntity<List<DtoOrder>> findOrdersByUserId(Long userId);

    RootEntity<DtoOrder> updateOrder(Long id, DtoOrderIU input);

    RootEntity<Void> deleteOrder(Long id);

    RootEntity<DtoOrder> confirmOrderPayment(Long orderId);

    RootEntity<DtoOrder> cancelOrder(Long orderId);

    RootEntity<OrderStatus> getOrderStatus(Long orderId);

    RootEntity<RestPageableEntity<DtoOrder>> findPageableOrdersBySeller(Long sellerId, RestPageableRequest pageableRequest, String status);
}
