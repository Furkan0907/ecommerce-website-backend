package com.furkan.dto.request;

import com.furkan.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
public class DtoPaymentIU {

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentMethod method;

    private String transactionId;
}
