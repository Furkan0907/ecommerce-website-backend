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
public class DtoCartItem extends DtoBase {

    private DtoProduct product;

    private Integer quantity;
}
