package com.furkan.service;

import com.furkan.dto.request.DtoPaymentIU;
import com.furkan.dto.response.DtoPayment;

import java.util.List;

public interface IPaymentService {

    DtoPayment createPayment(DtoPaymentIU input);

    DtoPayment findPaymentByOrderId(Long orderId);

    List<DtoPayment> findPaymentsByUserId(Long userId);

    boolean hasSuccessfulPayment(Long orderId);
}
