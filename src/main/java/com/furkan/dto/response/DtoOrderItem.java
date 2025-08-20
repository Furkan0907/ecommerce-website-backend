package com.furkan.dto.response;

import com.furkan.dto.DtoBase;
import com.furkan.enums.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoOrderItem extends DtoBase {

    private DtoProduct product;

    private Integer quantity;

    private BigDecimal price;

    private OrderItemStatus status;
}
