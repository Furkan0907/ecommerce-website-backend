package com.furkan.controller;

import com.furkan.dto.request.DtoProductIU;
import com.furkan.dto.response.DtoProduct;
import com.furkan.dto.response.DtoUser;
import com.furkan.utils.RestPageableEntity;
import com.furkan.utils.RestPageableRequest;
import com.furkan.utils.RootEntity;

import java.math.BigDecimal;
import java.util.List;

public interface IRestProductController {

    RootEntity<DtoProduct> saveProduct(DtoProductIU input);

    RootEntity<DtoProduct> findProductById(Long id);

    RootEntity<List<DtoProduct>> findAllProducts();

    RootEntity<DtoProduct> findProductByName(String name);

    RootEntity<List<DtoProduct>> findProductsByBrand(String brand);

    RootEntity<List<DtoProduct>> findProductsByCategory(String category);

    RootEntity<List<DtoProduct>> findProductsByPriceRange(BigDecimal min, BigDecimal max);

    RootEntity<List<DtoProduct>> findProductsByAvailability(boolean available);

    RootEntity<DtoProduct> updateProductById(Long id, DtoProductIU input);

    RootEntity<Void> deleteProductById(Long id);

    RootEntity<Long> countProducts();

    RootEntity<RestPageableEntity<DtoProduct>> findProductsPaged(RestPageableRequest request);

    RootEntity<List<DtoProduct>> findByFilter(String brand, String category, BigDecimal minPrice, BigDecimal maxPrice);

    RootEntity<List<DtoProduct>> findProductsBySellerId(Long sellerId);

    RootEntity<Long> countProductsBySellerId(Long sellerId);

    RootEntity<RestPageableEntity<DtoProduct>> findPageableProductsBySellerId(Long sellerId, RestPageableRequest pageableRequest);

    RootEntity<DtoUser> findSellerByProductId(Long productId);
}
