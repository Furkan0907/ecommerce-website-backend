package com.furkan.dto.request;

import com.furkan.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DtoPaymentIU {

    @NotNull
    private Long userId;

    @NotNull
    private Long orderId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;
}
