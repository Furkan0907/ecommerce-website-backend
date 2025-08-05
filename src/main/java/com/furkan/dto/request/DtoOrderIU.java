package com.furkan.dto.request;

import com.furkan.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoOrderIU {

    @NotNull
    private Long userId;
}
