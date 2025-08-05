package com.furkan.service.impl;

import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoPayment;
import com.furkan.enums.OrderStatus;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Order;
import com.furkan.model.Payment;
import com.furkan.repository.OrderRepository;
import com.furkan.repository.PaymentRepository;
import com.furkan.service.IPaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    private DtoPayment dtoConverter(Payment payment) {
        DtoPayment dtoPayment = new DtoPayment();
        BeanUtils.copyProperties(payment, dtoPayment);
        dtoPayment.setOrderId(payment.getOrder().getId());
        return dtoPayment;
    }

    @Override
    public DtoPayment createPayment(DtoPaymentIU input) {
        Payment payment = new Payment();

        Order order = orderRepository.findById(input.getOrderId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.ORDER_NOT_FOUND, input.getOrderId().toString())));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BaseException(new ErrorMessage(MessageType.ORDER_ALREADY_CANCELLED, input.getOrderId().toString()));
        }

        payment.setOrder(order);
        payment.setCreatedAt(new Date());
        payment.setAmount(order.getTotalAmount());
        payment.setSuccess(true);
        payment.setMethod(input.getMethod());
        payment.setUpdatedAt(new Date());

        Payment saved = paymentRepository.save(payment);
        return dtoConverter(saved);
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
        return payment.isSuccess();
    }
}
