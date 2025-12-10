package com.furkan.repository;

import com.furkan.model.RefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {

    List<RefundRequest> findAllByUserId(Long userId);

    List<RefundRequest> findAllByOrderItem_Product_SellerId(Long sellerId);

    Optional<RefundRequest> findByIdAndOrderItem_Product_Seller_Id(Long id, Long sellerId);
}
