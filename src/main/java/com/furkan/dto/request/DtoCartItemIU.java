package com.furkan.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCartItemIU {

    @NotNull
    private Long productId;

    @NotNull
    private Long cartId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
