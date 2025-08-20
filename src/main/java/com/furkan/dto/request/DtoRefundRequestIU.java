package com.furkan.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoRefundRequestIU {

    @NotNull
    private Long orderItemId;

    @NotBlank
    private String reason;
}
