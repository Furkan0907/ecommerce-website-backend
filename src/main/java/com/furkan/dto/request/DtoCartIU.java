package com.furkan.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCartIU {

    @NotNull
    private Long userId;
}
