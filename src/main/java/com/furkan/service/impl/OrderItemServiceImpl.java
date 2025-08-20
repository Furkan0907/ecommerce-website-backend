package com.furkan.service.impl;

import com.furkan.dto.request.DtoOrderItemIU;
import com.furkan.dto.response.DtoOrderItem;
import com.furkan.dto.response.DtoProduct;
import com.furkan.dto.response.DtoUser;
import com.furkan.enums.OrderItemStatus;
import com.furkan.enums.OrderStatus;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Order;
import com.furkan.model.OrderItem;
import com.furkan.model.Product;
import com.furkan.model.User;
import com.furkan.repository.OrderItemRepository;
import com.furkan.repository.OrderRepository;
import com.furkan.repository.ProductRepository;
import com.furkan.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Override
    public DtoOrderItem createOrderItem(DtoOrderItemIU input) {
        Order order = orderRepository.findById(input.getOrderId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, input.getOrderId().toString())));

        Product product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, input.getProductId().toString())));

        User seller = userRepository.findById(product.getSeller().getId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, product.getSeller().getId().toString())));

        product.setSeller(seller);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(input.getQuantity());
        orderItem.setPrice(product.getPrice());
        orderItem.setStatus(OrderItemStatus.PENDING);
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
            if (dtoProduct.getSeller() != null) {
                DtoUser dtoSeller = new DtoUser();
                BeanUtils.copyProperties(dtoProduct.getSeller(), dtoSeller);
                dtoProduct.setSeller(dtoSeller);
            }
            dtoOrderItem.setProduct(dtoProduct);
        }

        return dtoOrderItem;
    }

    @Override
    public DtoOrderItem cancelOrderItem(Long id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_NOT_FOUND, id.toString())));

        if (item.getStatus() != OrderItemStatus.CONFIRMED) {
            throw new BaseException(new ErrorMessage(MessageType.CANNOT_CANCEL_ITEM, id.toString()));
        }

        item.setStatus(OrderItemStatus.CANCELLED);

        Product product = item.getProduct();
        product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        productRepository.save(product);

        item.setUpdatedAt(new Date());
        OrderItem savedItem = orderItemRepository.save(item);

        updateOrderAggregateStatus(item.getOrder());

        return dtoConverter(savedItem);
    }

    @Override
    public OrderItem refundOrderItem(Long id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_NOT_FOUND, id.toString())));

        if (item.getStatus() != OrderItemStatus.RETURN_REQUESTED) {
            throw new BaseException(new ErrorMessage(MessageType.CANNOT_REFUND_ITEM, id.toString()));
        }

        item.setStatus(OrderItemStatus.RETURNED);

        Product product = item.getProduct();
        product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        productRepository.save(product);

        item.setUpdatedAt(new Date());
        OrderItem savedItem = orderItemRepository.save(item);

        updateOrderAggregateStatus(item.getOrder());

        return savedItem;
    }

    @Override
    public DtoOrderItem deliverOrderItem(Long id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_NOT_FOUND, id.toString())));

        if (item.getStatus() != OrderItemStatus.SHIPPED) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_MUST_BE_SHIPPED, id.toString()));
        }

        item.setStatus(OrderItemStatus.DELIVERED);
        item.setUpdatedAt(new Date());
        OrderItem savedItem = orderItemRepository.save(item);

        updateOrderAggregateStatus(item.getOrder());

        return dtoConverter(savedItem);
    }

    @Override
    public DtoOrderItem markOrderItemShipped(Long id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_NOT_FOUND, id.toString())));

        if (item.getStatus() != OrderItemStatus.CONFIRMED) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ITEM_MUST_BE_CONFIRMED, id.toString()));
        }

        item.setStatus(OrderItemStatus.SHIPPED);
        item.setUpdatedAt(new Date());
        OrderItem savedItem = orderItemRepository.save(item);

        updateOrderAggregateStatus(item.getOrder());

        return dtoConverter(savedItem);
    }

    @Override
    public void updateOrderAggregateStatus(Order input) {
        Order order = orderRepository.findWithItems(input.getId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, input.getId().toString())));

        boolean allConfirmed = order.getOrderItems().stream()
                .allMatch(i -> i.getStatus() == OrderItemStatus.CONFIRMED);
        boolean allShipped = order.getOrderItems().stream()
                .allMatch(i -> i.getStatus() == OrderItemStatus.SHIPPED);
        boolean allDelivered = order.getOrderItems().stream()
                .allMatch(i -> i.getStatus() == OrderItemStatus.DELIVERED);
        boolean allReturned = order.getOrderItems().stream()
                .allMatch(i -> i.getStatus() == OrderItemStatus.RETURNED);
        boolean allRefundRejected = order.getOrderItems().stream()
                .allMatch(i -> i.getStatus() == OrderItemStatus.REFUND_REJECTED);

        boolean anyShipped = order.getOrderItems().stream()
                .anyMatch(i -> i.getStatus() == OrderItemStatus.SHIPPED);
        boolean anyDelivered = order.getOrderItems().stream()
                .anyMatch(i -> i.getStatus() == OrderItemStatus.DELIVERED);
        boolean anyReturnRequested = order.getOrderItems().stream()
                .anyMatch(i -> i.getStatus() == OrderItemStatus.RETURN_REQUESTED);
        boolean anyReturned = order.getOrderItems().stream()
                .anyMatch(i -> i.getStatus() == OrderItemStatus.RETURNED);


        if (allReturned) {
            order.setStatus(OrderStatus.REFUNDED);
        } else if (anyReturned) {
            order.setStatus(OrderStatus.PARTIALLY_REFUNDED);
        } else if (allDelivered) {
            order.setStatus(OrderStatus.DELIVERED);
        } else if (anyDelivered) {
            order.setStatus(OrderStatus.PARTIALLY_DELIVERED);
        } else if (allShipped) {
            order.setStatus(OrderStatus.SHIPPED);
        } else if (anyShipped) {
            order.setStatus(OrderStatus.PARTIALLY_SHIPPED);
        } else if (allConfirmed) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else if (anyReturnRequested) {
            order.setStatus(OrderStatus.PARTIALLY_RETURN_REQUESTED);
        } else if (allRefundRejected) {
            order.setStatus(OrderStatus.DELIVERED);
        } else {
            order.setStatus(OrderStatus.PENDING);
        }

        order.setUpdatedAt(new Date());
        orderRepository.save(order);
    }
}
