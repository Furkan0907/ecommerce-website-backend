package com.furkan.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoAddressIU {

    @NotNull
    private Long userId;

    @NotNull
    private String city;

    @NotNull
    private String district;

    @NotNull
    private String fullAddress;

    @NotNull
    private String phoneNumber;
}
