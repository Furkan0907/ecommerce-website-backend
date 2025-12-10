package com.furkan.repository;

import com.furkan.enums.OrderStatus;
import com.furkan.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    @Query("""
    SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
    FROM Order o JOIN o.orderItems oi
    WHERE o.user.id = :userId AND oi.product.id = :productId AND o.status = 'DELIVERED'
    """)
    boolean existsByUserIdAndProductIdConfirmedOrders(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findWithItems(@Param("id") Long id);

    Page<Order> findDistinctByOrderItems_Product_Seller_Id(Long sellerId, Pageable pageable);

    Page<Order> findDistinctByOrderItems_Product_Seller_IdAndStatus(Long sellerId, OrderStatus status, Pageable pageable);

    @Query(value = "from Order")
    Page<Order> findAllPageable(Pageable pageable);
}
