package com.furkan.service;

import com.furkan.dto.request.DtoProductIU;
import com.furkan.dto.response.DtoProduct;
import com.furkan.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface IProductService {

    List<DtoProduct> dtoListConverter(List<Product> input);

    DtoProduct saveProduct(DtoProductIU input);

    DtoProduct findProductById(Long id);

    List<DtoProduct> findAllProducts();

    DtoProduct findProductByName(String name);

    List<DtoProduct> findProductsByBrand(String brand);

    List<DtoProduct> findProductsByCategory(String category);

    List<DtoProduct> findProductsByPriceRange(BigDecimal min, BigDecimal max);

    List<DtoProduct> findProductsByAvailability(boolean available);

    DtoProduct updateProductById(Long id, DtoProductIU input);

    void deleteProductById(Long id);

    long countProducts();

    Page<Product> findProductsPaged(Pageable pageable);

    List<DtoProduct> findByFilter(String brand, String category, BigDecimal minPrice, BigDecimal maxPrice);
}
