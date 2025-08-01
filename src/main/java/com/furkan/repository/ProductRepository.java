package com.furkan.repository;

import com.furkan.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    List<Product> findByBrand(String brand);

    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findByAvailable(boolean available);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    @Query("SELECT p FROM Product p WHERE (:brand IS NULL OR p.brand = :brand) AND (:category IS NULL OR p.category = :category) AND (p.price BETWEEN :minPrice AND :maxPrice)")
    List<Product> findByFilter(@Param("brand") String brand,
                               @Param("category") String category,
                               @Param("minPrice") BigDecimal minPrice,
                               @Param("maxPrice") BigDecimal maxPrice);

    List<Product> findByStockQuantityGreaterThan(int quantity);

    List<Product> findByStockQuantityEquals(int quantity);

    @Query(value = "from Product")
    Page<Product> findAllPageable(Pageable pageable);
}
