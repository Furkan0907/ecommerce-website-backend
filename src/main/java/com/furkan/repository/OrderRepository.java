package com.furkan.repository;

import com.furkan.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    @Query("""
    SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
    FROM Order o JOIN o.orderItems oi
    WHERE o.user.id = :userId AND oi.product.id = :productId AND o.status = 'DELIVERED'
    """)
    boolean existsByUserIdAndProductIdConfirmedOrders(@Param("userId") Long userId, @Param("productId") Long productId);
}
