package com.furkan.service.impl;

import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoPayment;
import com.furkan.enums.OrderStatus;
import com.furkan.enums.PaymentMethod;
import com.furkan.enums.PaymentStatus;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Order;
import com.furkan.model.Payment;
import com.furkan.repository.OrderRepository;
import com.furkan.repository.PaymentRepository;
import com.furkan.service.IOrderService;
import com.furkan.service.IPaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IOrderService orderService;

    private DtoPayment dtoConverter(Payment payment) {
        DtoPayment dtoPayment = new DtoPayment();
        BeanUtils.copyProperties(payment, dtoPayment);
        dtoPayment.setOrderId(payment.getOrder().getId());
        dtoPayment.setTransactionId(payment.getTransactionId());
        dtoPayment.setStatus(payment.getStatus());
        return dtoPayment;
    }

    private boolean simulatePayment(DtoPaymentIU input) {
        return input.getMethod() != PaymentMethod.CASH_ON_DELIVERY;
    }

    @Override
    public DtoPayment createPayment(DtoPaymentIU input) {
        Order order = orderRepository.findById(input.getOrderId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, input.getOrderId().toString())));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ALREADY_CANCELLED, order.getId().toString()));
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_IS_NOT_PENDING, order.getId().toString()));
        }


        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setCreatedAt(new Date());
        payment.setUpdatedAt(new Date());
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(input.getMethod());
        payment.setTransactionId(input.getTransactionId());

        payment.setStatus(PaymentStatus.PENDING);

        boolean isPaymentSuccessful = simulatePayment(input);

        if (!isPaymentSuccessful) {
            payment.setStatus(PaymentStatus.FAILED);
            throw new BaseException(new ErrorMessage(MessageType.PAYMENT_FAILED, order.getId().toString()));
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        Payment savedPayment = paymentRepository.save(payment);

        orderService.confirmOrderPayment(order.getId());

        return dtoConverter(savedPayment);
    }

    @Override
    public DtoPayment findPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_PAYMENT_FOUND_FOR_THIS_ORDER, orderId.toString())));
        return dtoConverter(payment);
    }

    @Override
    public List<DtoPayment> findPaymentsByUserId(Long userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        if (payments.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PAYMENT_LIST_IS_EMPTY_FOR_THIS_USER, userId.toString()));
        }
        List<DtoPayment> dtoPaymentList = new ArrayList<>();
        for (Payment payment : payments) {
            dtoPaymentList.add(dtoConverter(payment));
        }
        return dtoPaymentList;
    }

    @Override
    public boolean hasSuccessfulPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_PAYMENT_FOUND_FOR_THIS_ORDER, orderId.toString())));
        return payment.getStatus() == PaymentStatus.COMPLETED;
    }

    @Override
    public DtoPayment updatePaymentStatus(Long orderId, PaymentStatus newStatus, String transactionId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_PAYMENT_FOUND_FOR_THIS_ORDER, orderId.toString())));

        payment.setStatus(newStatus);
        if (transactionId != null) {
            payment.setTransactionId(transactionId);
        }
        payment.setUpdatedAt(new Date());

        Payment saved = paymentRepository.save(payment);

        if (newStatus == PaymentStatus.COMPLETED) {
            orderService.confirmOrderPayment(orderId);
        }
        return dtoConverter(saved);
    }

    @Override
    public DtoPayment refundPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_PAYMENT_FOUND_FOR_THIS_ORDER, orderId.toString())));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BaseException(new ErrorMessage(MessageType.PAYMENT_NOT_COMPLETED, orderId.toString()));
        }


        boolean refundSuccess = simulateRefund(payment);
        if (!refundSuccess) {
            throw new BaseException(new ErrorMessage(MessageType.REFUND_FAILED, orderId.toString()));
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(new Date());

        Payment refundedPayment = paymentRepository.save(payment);

        Order order = refundedPayment.getOrder();
        order.setStatus(OrderStatus.REFUNDED);
        order.setUpdatedAt(new Date());
        orderRepository.save(order);

        return dtoConverter(refundedPayment);
    }

    private boolean simulateRefund(Payment payment) {
        return true;
    }
}
