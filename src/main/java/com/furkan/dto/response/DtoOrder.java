package com.furkan.dto.response;

import com.furkan.dto.DtoBase;
import com.furkan.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoOrder extends DtoBase {

    private DtoUser user;

    private DtoAddress address;

    private List<DtoOrderItem> orderItems;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private DtoPayment payment;
}
