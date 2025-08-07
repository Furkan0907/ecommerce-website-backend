package com.furkan.dto.response;

import com.furkan.dto.DtoBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoAddress extends DtoBase {

    private DtoUser user;

    private String city;

    private String district;

    private String fullAddress;

    private String phoneNumber;
}
