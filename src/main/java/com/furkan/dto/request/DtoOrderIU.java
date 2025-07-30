package com.furkan.dto.request;

import com.furkan.enums.OrderStatus;
import com.furkan.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DtoOrderIU {

    @NotNull
    private Long userId;

    @NotEmpty
    private List<DtoOrderItemIU> orderItems;

    @NotNull
    private OrderStatus status;

    @NotNull
    private PaymentMethod paymentMethod;
}
