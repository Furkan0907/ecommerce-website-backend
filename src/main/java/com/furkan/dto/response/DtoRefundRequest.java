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
public class DtoRefundRequest extends DtoBase {

    private DtoUser user;


    private DtoOrder order;

    private String reason;

    private boolean approved;
}
