package com.furkan.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoReviewIU {

    @NotNull
    private Long productId;

    @NotNull
    private Long userId;

    @NotBlank
    private String content;

    @Min(1)
    @Max(5)
    private int rating;
}
