package com.furkan.service;

import com.furkan.dto.request.DtoOrderIU;
import com.furkan.dto.response.DtoOrder;
import com.furkan.enums.OrderStatus;
import com.furkan.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IOrderService {

    DtoOrder createOrder(DtoOrderIU input);

    DtoOrder findOrderById(Long id);

    Page<Order> findAllOrders(Pageable pageable);

    List<DtoOrder> findOrdersByUserId(Long userId);

    DtoOrder updateOrder(Long id, DtoOrderIU input);

    void deleteOrder(Long id);

    DtoOrder confirmOrderPayment(Long orderId);

    DtoOrder cancelOrder(Long orderId);

    OrderStatus getOrderStatus(Long orderId);

    Page<Order> findOrdersBySeller(Long sellerId, Pageable pageable, String status);

    DtoOrder dtoConverterForSeller(Order order, Long sellerId);

    List<DtoOrder> dtoListConverter(List<Order> orderList);

    Map<String, Double> getMonthlySalesStatistics();
}
