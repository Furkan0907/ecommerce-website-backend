package com.furkan.service.impl;

import com.furkan.dto.request.DtoReviewIU;
import com.furkan.dto.response.DtoProduct;
import com.furkan.dto.response.DtoReview;
import com.furkan.dto.response.DtoUser;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Product;
import com.furkan.model.Review;
import com.furkan.model.User;
import com.furkan.repository.OrderRepository;
import com.furkan.repository.ProductRepository;
import com.furkan.repository.ReviewRepository;
import com.furkan.repository.UserRepository;
import com.furkan.service.IReviewService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private DtoReview dtoConverter(Review review) {
        DtoReview dtoReview = new DtoReview();
        BeanUtils.copyProperties(review, dtoReview);

        if (review.getUser() != null) {
            DtoUser dtoUser = new DtoUser();
            BeanUtils.copyProperties(review.getUser(), dtoUser);
            dtoReview.setUser(dtoUser);
        }

        if (review.getProduct() != null) {
            DtoProduct dtoProduct = new DtoProduct();
            BeanUtils.copyProperties(review.getProduct(), dtoProduct);
            dtoReview.setProduct(dtoProduct);
        }

        return dtoReview;
    }


    @Override
    public List<DtoReview> dtoListConverter(List<Review> reviews) {
        return reviews.stream()
                .map(this::dtoConverter)
                .collect(Collectors.toList());
    }

    @Override
    public DtoReview save(DtoReviewIU input) {
        User user = userRepository.findById(input.getUserId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.USER_NOT_FOUND, input.getUserId().toString())));

        Product product = productRepository.findById(input.getProductId())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, input.getProductId().toString())));

        boolean alreadyReviewed = reviewRepository.existsByUserIdAndProductId(user.getId(), product.getId());
        if (alreadyReviewed) {
            throw new BaseException(new ErrorMessage(MessageType.ALREADY_REVIEWED, null));
        }

        if (!orderRepository.existsByUserIdAndProductIdConfirmedOrders(user.getId(), product.getId())) {
            throw new BaseException(new ErrorMessage(MessageType.USER_NOT_BOUGHT_PRODUCT, null));
        }

        Review review = new Review();
        BeanUtils.copyProperties(input, review);
        review.setUser(user);
        review.setProduct(product);
        review.setCreatedAt(new Date());
        review.setUpdatedAt(new Date());

        Review saved = reviewRepository.save(review);
        return dtoConverter(saved);
    }

    @Override
    public DtoReview findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REVIEW_NOT_FOUND, id.toString())));
        return dtoConverter(review);
    }

    @Override
    public DtoReview update(Long id, DtoReviewIU input) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REVIEW_NOT_FOUND, id.toString())));
        review.setRating(input.getRating());
        review.setContent(input.getContent());
        review.setUpdatedAt(new Date());

        Review updated = reviewRepository.save(review);
        return dtoConverter(updated);
    }

    @Override
    public void delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.REVIEW_NOT_FOUND, id.toString())));
        reviewRepository.delete(review);
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    public Page<Review> findByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable);
    }

    @Override
    public List<DtoReview> findByUserId(Long userId) {
        List<DtoReview> dtoReviews = new ArrayList<>();
        List<Review> reviews = reviewRepository.findByUserId(userId);
        if (reviews.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.NO_REVIEW_FOUND_FOR_THIS_USER, userId.toString()));
        }
        for (Review review : reviews) {
            dtoReviews.add(dtoConverter(review));
        }
        return dtoReviews;
    }

    @Override
    public boolean existsByUserIdAndProductId(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public double getAverageRatingForProduct(Long productId) {
        Double average = reviewRepository.findAverageRatingByProductId(productId);
        return average != null ? average : 0.0;
    }
}
