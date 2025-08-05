package com.furkan.service;

import com.furkan.dto.request.DtoOrderIU;
import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoOrder;
import com.furkan.enums.OrderStatus;

import java.util.List;

public interface IOrderService {

    DtoOrder createOrder(DtoOrderIU input);

    DtoOrder findOrderById(Long id);

    List<DtoOrder> findAllOrders();

    List<DtoOrder> findOrdersByUserId(Long userId);

    DtoOrder updateOrder(Long id, DtoOrderIU input);

    void deleteOrder(Long id);

    DtoOrder confirmOrderPayment(Long orderId, DtoPaymentIU paymentInfo);

    DtoOrder cancelOrder(Long orderId);

    OrderStatus getOrderStatus(Long orderId);
}
