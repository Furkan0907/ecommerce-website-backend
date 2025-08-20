package com.furkan.service.impl;

import com.furkan.dto.request.DtoOrderIU;
import com.furkan.dto.response.*;
import com.furkan.enums.OrderItemStatus;
import com.furkan.enums.OrderStatus;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.*;
import com.furkan.repository.*;
import com.furkan.service.IOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    private DtoOrder dtoConverter(Order order) {
        DtoOrder dtoOrder = new DtoOrder();
        BeanUtils.copyProperties(order, dtoOrder);

        if (order.getUser() != null) {
            DtoUser dtoUser = new DtoUser();
            BeanUtils.copyProperties(order.getUser(), dtoUser);
            dtoOrder.setUser(dtoUser);
        }

        if (order.getAddress() != null) {
            DtoAddress dtoAddress = new DtoAddress();
            BeanUtils.copyProperties(order.getAddress(), dtoAddress);
            dtoOrder.setAddress(dtoAddress);
        }

        if (order.getOrderItems() != null) {
            List<DtoOrderItem> dtoOrderItems = order.getOrderItems().stream().map(item -> {
                DtoOrderItem dtoItem = new DtoOrderItem();
                BeanUtils.copyProperties(item, dtoItem);

                if (item.getProduct() != null) {
                    DtoProduct dtoProduct = new DtoProduct();
                    BeanUtils.copyProperties(item.getProduct(), dtoProduct);
                    dtoItem.setProduct(dtoProduct);
                }

                return dtoItem;
            }).collect(Collectors.toList());

            dtoOrder.setOrderItems(dtoOrderItems);
        }

        if (order.getPayments() != null) {
            DtoPayment payment = new DtoPayment();
            BeanUtils.copyProperties(order.getPayments(), payment);
            payment.setUpdatedAt(new Date());
            dtoOrder.setPayment(payment);
        }

        return dtoOrder;
    }

    @Override
    public DtoOrder createOrder(DtoOrderIU input) {
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, input.getUserId().toString())));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_CARD_FOUND_FOR_THIS_USER, user.getId().toString())));

        Address address = addressRepository.findById(input.getAddressId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ADDRESS_NOT_FOUND, input.getAddressId().toString())));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.CART_LIST_IS_EMPTY, null));
        }

        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        order.setStatus(OrderStatus.PENDING);
        order.setAddress(address);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setStatus(OrderItemStatus.PENDING);
            orderItem.setCreatedAt(new Date());
            orderItem.setUpdatedAt(new Date());

            orderItems.add(orderItem);

            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order saved = orderRepository.save(order);
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return dtoConverter(saved);
    }

    @Override
    public DtoOrder findOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, id.toString())));
        return dtoConverter(order);
    }

    @Override
    public List<DtoOrder> findAllOrders() {
        List<DtoOrder> dtoList = new ArrayList<>();
        List<Order> orderList = orderRepository.findAll();
        for (Order order : orderList) {
            dtoList.add(dtoConverter(order));
        }
        return dtoList;
    }

    @Override
    public List<DtoOrder> findOrdersByUserId(Long userId) {
        List<DtoOrder> dtoOrders = new ArrayList<>();
        List<Order> orderList = orderRepository.findByUserId(userId);
        for (Order order : orderList) {
            dtoOrders.add(dtoConverter(order));
        }
        return dtoOrders;
    }

    @Override
    public DtoOrder updateOrder(Long id, DtoOrderIU input) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, id.toString())));

        if (!order.getUser().getId().equals(input.getUserId())) {
            throw new BaseException(new ErrorMessage(MessageType.NO_ORDER_FOUND_FOR_THIS_USER, input.getUserId().toString()));
        }

        if (order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ALREADY_COMPLETED, id.toString()));
        }

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ALREADY_CANCELLED, id.toString()));
        }

        Address address = addressRepository.findById(input.getAddressId())
                        .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ADDRESS_NOT_FOUND, input.getAddressId().toString())));
        order.setAddress(address);
        order.setUpdatedAt(new Date());

        Order updated = orderRepository.save(order);
        return dtoConverter(updated);
    }

    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, id.toString())));
        orderRepository.delete(order);
    }

    @Override
    public DtoOrder confirmOrderPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, orderId.toString())));


        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ALREADY_CANCELLED, orderId.toString()));
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_IS_NOT_PENDING, orderId.toString()));
        }

        for (OrderItem item : order.getOrderItems()) {
            if (item.getStatus() != OrderItemStatus.PENDING) continue;

            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BaseException(new ErrorMessage(MessageType.OUT_OF_STOCK, product.getId().toString()));
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            item.setStatus(OrderItemStatus.CONFIRMED);
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(new Date());

        Order saved = orderRepository.save(order);
        return dtoConverter(saved);
    }

    @Override
    public DtoOrder cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, orderId.toString())));

        if (order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ALREADY_COMPLETED, orderId.toString()));
        }

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ALREADY_CANCELLED, orderId.toString()));
        }

        if (!order.getStatus().equals(OrderStatus.CONFIRMED)) {
            throw new BaseException(new ErrorMessage(MessageType.CAN_NOT_CANCEL_ORDER, orderId.toString()));
        }

        for (OrderItem item: order.getOrderItems()) {
            if (item.getStatus() == OrderItemStatus.CONFIRMED) {
                item.setStatus(OrderItemStatus.CANCELLED);
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);

        order.setUpdatedAt(new Date());

        Order saved = orderRepository.save(order);
        return dtoConverter(saved);
    }

    @Override
    public OrderStatus getOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, orderId.toString())));

        return order.getStatus();
    }
}
