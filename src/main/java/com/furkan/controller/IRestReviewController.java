package com.furkan.controller;

import com.furkan.dto.request.DtoReviewIU;
import com.furkan.dto.response.DtoReview;
import com.furkan.utils.RestPageableEntity;
import com.furkan.utils.RestPageableRequest;
import com.furkan.utils.RootEntity;

import java.util.List;

public interface IRestReviewController {

    RootEntity<DtoReview> save(DtoReviewIU input);

    RootEntity<DtoReview> findById(Long id);

    RootEntity<DtoReview> update(Long id, DtoReviewIU input);

    RootEntity<Void> delete(Long id);

    RootEntity<RestPageableEntity<DtoReview>> findAll(RestPageableRequest pageableRequest);

    RootEntity<RestPageableEntity<DtoReview>> findByProductId(Long productId, RestPageableRequest pageableRequest);

    RootEntity<List<DtoReview>> findByUserId(Long userId);

    RootEntity<Boolean> existsByUserIdAndProductId(Long userId, Long productId);

    RootEntity<Double> getAverageRatingForProduct(Long productId);
}
