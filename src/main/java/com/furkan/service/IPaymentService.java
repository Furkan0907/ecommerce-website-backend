package com.furkan.service;

import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoPayment;
import com.furkan.enums.PaymentStatus;
import com.stripe.exception.StripeException;

import java.util.List;

public interface IPaymentService {

    DtoPayment createPayment(DtoPaymentIU input);

    DtoPayment findPaymentByOrderId(Long orderId);

    List<DtoPayment> findPaymentsByUserId(Long userId);

    boolean hasSuccessfulPayment(Long orderId);

    DtoPayment updatePaymentStatus(Long orderId, PaymentStatus newStatus, String transactionId);

    DtoPayment refundPaymentItem(Long orderItemId) throws StripeException;

    void handleStripeWebhook(String payload, String sigHeader);
}
