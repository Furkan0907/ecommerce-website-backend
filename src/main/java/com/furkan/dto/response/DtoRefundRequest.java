package com.furkan.dto.response;

import com.furkan.dto.DtoBase;
import com.furkan.enums.RefundRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoRefundRequest extends DtoBase {

    private Long orderId;

    private DtoOrderItem orderItem;

    private Long userId;

    private String reason;

    private RefundRequestStatus status;
}
