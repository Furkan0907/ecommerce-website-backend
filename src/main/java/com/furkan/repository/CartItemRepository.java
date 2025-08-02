package com.furkan.repository;

import com.furkan.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    List<CartItem> findByCartId(Long cartId);

    @Query("""
        SELECT ci FROM CartItem ci
        JOIN FETCH ci.product
        JOIN FETCH ci.cart c
        JOIN FETCH c.user
        LEFT JOIN FETCH c.cartItems
        WHERE ci.id = :id
    """)
    Optional<CartItem> findWithAllRelations(@Param("id") Long id);
}
