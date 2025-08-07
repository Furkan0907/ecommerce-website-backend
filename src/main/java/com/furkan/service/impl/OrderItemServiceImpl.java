package com.furkan.service.impl;

import com.furkan.dto.request.DtoOrderItemIU;
import com.furkan.dto.response.DtoOrderItem;
import com.furkan.dto.response.DtoProduct;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Order;
import com.furkan.model.OrderItem;
import com.furkan.model.Product;
import com.furkan.repository.OrderItemRepository;
import com.furkan.repository.OrderRepository;
import com.furkan.repository.ProductRepository;
import com.furkan.service.IOrderItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemServiceImpl implements IOrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public DtoOrderItem createOrderItem(DtoOrderItemIU input) {
        Order order = orderRepository.findById(input.getOrderId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, input.getOrderId().toString())));

        Product product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, input.getProductId().toString())));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(input.getQuantity());
        orderItem.setPrice(product.getPrice());
        orderItem.setCreatedAt(new Date());
        orderItem.setUpdatedAt(new Date());

        OrderItem saved = orderItemRepository.save(orderItem);
        return dtoConverter(saved);
    }

    @Override
    public DtoOrderItem findOrderItemById(Long id) {
        OrderItem orderItem = orderItemRepository.findWithAllRelations(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_NOT_FOUND, id.toString())));
        return dtoConverter(orderItem);
    }

    @Override
    public List<DtoOrderItem> findAllByOrderId(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItems.stream()
                .map(this::dtoConverter)
                .collect(Collectors.toList());
    }

    @Override
    public DtoOrderItem updateOrderItem(Long id, DtoOrderItemIU input) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_NOT_FOUND, id.toString())));
        orderItem.setUpdatedAt(new Date());
        orderItem.setQuantity(input.getQuantity());

        Product product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, input.getProductId().toString())));
        orderItem.setProduct(product);
        orderItem.setPrice(product.getPrice());

        Order order = orderRepository.findById(input.getOrderId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, input.getOrderId().toString())));
        orderItem.setOrder(order);
        OrderItem updated = orderItemRepository.save(orderItem);
        return dtoConverter(updated);
    }

    @Override
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_NOT_FOUND, id.toString())));
        orderItemRepository.delete(orderItem);
    }

    @Override
    public DtoOrderItem dtoConverter(OrderItem orderItem) {
        DtoOrderItem dtoOrderItem = new DtoOrderItem();
        BeanUtils.copyProperties(orderItem, dtoOrderItem);

        if (orderItem.getProduct() != null) {
            DtoProduct dtoProduct = new DtoProduct();
            BeanUtils.copyProperties(orderItem.getProduct(), dtoProduct);
            dtoOrderItem.setProduct(dtoProduct);
        }

        return dtoOrderItem;
    }
}
