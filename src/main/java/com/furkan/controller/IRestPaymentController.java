package com.furkan.controller;

import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoPayment;
import com.furkan.enums.PaymentStatus;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestPaymentController {

    RootEntity<DtoPayment> createPayment(DtoPaymentIU input);

    RootEntity<DtoPayment> findPaymentByOrderId(Long orderId);

    RootEntity<List<DtoPayment>> findPaymentsByUserId(Long userId);

    RootEntity<Boolean> hasSuccessfulPayment(Long orderId);

    RootEntity<DtoPayment> updatePaymentStatus(Long orderId, PaymentStatus newStatus, String transactionId);

    RootEntity<DtoPayment> refundPayment(Long orderId);
}
