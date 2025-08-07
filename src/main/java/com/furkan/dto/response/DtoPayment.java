package com.furkan.dto.response;

import com.furkan.dto.DtoBase;
import com.furkan.enums.PaymentMethod;
import com.furkan.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoPayment extends DtoBase {

    private Long orderId;

    private BigDecimal amount;

    private PaymentMethod method;

    private String transactionId;

    private PaymentStatus status;
}
