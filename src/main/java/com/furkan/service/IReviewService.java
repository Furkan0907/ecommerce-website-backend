package com.furkan.service;

import com.furkan.dto.request.DtoReviewIU;
import com.furkan.dto.response.DtoReview;
import com.furkan.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReviewService {

    List<DtoReview> dtoListConverter(List<Review> reviews);

    DtoReview save(DtoReviewIU input);

    DtoReview findById(Long id);

    DtoReview update(Long id, DtoReviewIU input);

    void delete(Long id);

    Page<Review> findAll(Pageable pageable);

    Page<Review> findByProductId(Long productId, Pageable pageable);

    List<DtoReview> findByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    double getAverageRatingForProduct(Long productId);
}
