package com.furkan.controller.impl;

import com.furkan.controller.IRestReviewController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoReviewIU;
import com.furkan.dto.response.DtoReview;
import com.furkan.model.Review;
import com.furkan.service.IReviewService;
import com.furkan.utils.PagerUtil;
import com.furkan.utils.RestPageableEntity;
import com.furkan.utils.RestPageableRequest;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class RestReviewControllerImpl extends RestBaseController implements IRestReviewController {

    @Autowired
    private IReviewService reviewService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping()
    @Override
    public RootEntity<DtoReview> save(@Valid @RequestBody DtoReviewIU input) {
        return ok(reviewService.save(input));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{id}")
    @Override
    public RootEntity<DtoReview> findById(@PathVariable Long id) {
        return ok(reviewService.findById(id));
    }

    @PreAuthorize("@securityService.isReviewOwner(#id) or hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Override
    public RootEntity<DtoReview> update(@PathVariable Long id,@Valid @RequestBody DtoReviewIU input) {
        return ok(reviewService.update(id, input));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityService.isReviewOwner(#id)")
    @DeleteMapping("/{id}")
    @Override
    public RootEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ok();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @Override
    public RootEntity<RestPageableEntity<DtoReview>> findAll(@ModelAttribute RestPageableRequest pageableRequest) {
        Page<Review> page = reviewService.findAll(toPageable(pageableRequest));
        List<DtoReview> content = reviewService.dtoListConverter(page.getContent());
        RestPageableEntity<DtoReview> pageableResponse = toPageableResponse(page, content);
        return ok(pageableResponse);
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/product/{productId}/pageable")
    @Override
    public RootEntity<RestPageableEntity<DtoReview>> findByProductId(@PathVariable Long productId, @ModelAttribute RestPageableRequest pageableRequest) {
        Pageable pageable = PagerUtil.toPageable(pageableRequest);
        Page<Review> page = reviewService.findByProductId(productId, pageable);
        List<DtoReview> content = reviewService.dtoListConverter(page.getContent());
        RestPageableEntity<DtoReview> pageableResponse = toPageableResponse(page, content);
        return ok(pageableResponse);
    }

    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    @Override
    public RootEntity<List<DtoReview>> findByUserId(@PathVariable Long userId) {
        return ok(reviewService.findByUserId(userId));
    }

    @PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
    @GetMapping("/check-exists")
    @Override
    public RootEntity<Boolean> existsByUserIdAndProductId(@RequestParam Long userId, @RequestParam Long productId) {
        return ok(reviewService.existsByUserIdAndProductId(userId, productId));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/product/{productId}/average-rating")
    @Override
    public RootEntity<Double> getAverageRatingForProduct(@PathVariable Long productId) {
        return ok(reviewService.getAverageRatingForProduct(productId));
    }
}
