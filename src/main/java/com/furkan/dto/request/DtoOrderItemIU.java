package com.furkan.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DtoOrderItemIU {

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    private Long orderId;
}
