package com.furkan.controller;

import com.furkan.dto.request.DtoOrderIU;
import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoOrder;
import com.furkan.enums.OrderStatus;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestOrderController {

    RootEntity<DtoOrder> createOrder(DtoOrderIU input);

    RootEntity<DtoOrder> findOrderById(Long id);

    RootEntity<List<DtoOrder>> findAllOrders();

    RootEntity<List<DtoOrder>> findOrdersByUserId(Long userId);

    RootEntity<DtoOrder> updateOrder(Long id, DtoOrderIU input);

    RootEntity<Void> deleteOrder(Long id);

    RootEntity<DtoOrder> confirmOrderPayment(Long orderId, DtoPaymentIU paymentInfo);

    RootEntity<DtoOrder> cancelOrder(Long orderId);

    RootEntity<OrderStatus> getOrderStatus(Long orderId);

    RootEntity<DtoOrder> markOrderShipped(Long orderId);

    RootEntity<DtoOrder> deliverOrder(Long orderId);
}
