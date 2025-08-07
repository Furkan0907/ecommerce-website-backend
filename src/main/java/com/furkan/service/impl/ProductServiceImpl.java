package com.furkan.service.impl;

import com.furkan.dto.request.DtoProductIU;
import com.furkan.dto.response.DtoProduct;
import com.furkan.exception.BaseException;
import com.furkan.exception.ErrorMessage;
import com.furkan.exception.MessageType;
import com.furkan.model.Product;
import com.furkan.repository.ProductRepository;
import com.furkan.service.IProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    private Product createProduct(DtoProductIU input) {
        Product product = new Product();
        BeanUtils.copyProperties(input, product);
        product.setCreatedAt(new Date());
        product.setUpdatedAt(new Date());
        return product;
    }

    private DtoProduct dtoTransformation(Product product) {
        DtoProduct dtoProduct = new DtoProduct();
        BeanUtils.copyProperties(product, dtoProduct);
        return dtoProduct;
    }

    @Override
    public List<DtoProduct> dtoListConverter(List<Product> productList) {
        List<DtoProduct> dtoProductList = new ArrayList<>();
        for (Product dbProduct : productList) {
            dtoProductList.add(dtoTransformation(dbProduct));
        }
        return dtoProductList;
    }


    @Override
    public DtoProduct saveProduct(DtoProductIU input) {
        Product product = productRepository.save(createProduct(input));
        return dtoTransformation(product);
    }

    @Override
    public DtoProduct findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, id.toString())));
        return dtoTransformation(product);
    }

    @Override
    public List<DtoProduct> findAllProducts() {
        List<Product> productList = productRepository.findAll();
        return dtoListConverter(productList);
    }

    @Override
    public DtoProduct findProductByName(String name) {
        Product product = productRepository.findByName(name)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NAME_NOT_FOUND, name)));
        return dtoTransformation(product);
    }

    @Override
    public List<DtoProduct> findProductsByBrand(String brand) {
        List<Product> productList = productRepository.findByBrand(brand);
        if (productList.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PRODUCT_BRAND_NOT_FOUND, brand));
        }
        return dtoListConverter(productList);
    }

    @Override
    public List<DtoProduct> findProductsByCategory(String category) {
        List<Product> productList = productRepository.findByCategory(category);
        if (productList.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PRODUCT_CATEGORY_NOT_FOUND, category));
        }
        return dtoListConverter(productList);
    }

    @Override
    public List<DtoProduct> findProductsByPriceRange(BigDecimal min, BigDecimal max) {
        List<Product> productList = productRepository.findByPriceBetween(min, max);
        if (productList.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PRODUCT_PRICE_BETWEEN_NOT_FOUND, (min.toString() + ", " + max.toString())));
        }
        return dtoListConverter(productList);
    }

    @Override
    public List<DtoProduct> findProductsByAvailability(boolean available) {
        List<Product> productList;
        if (available) {
            productList = productRepository.findByStockQuantityGreaterThan(0);
        } else {
            productList = productRepository.findByStockQuantityEquals(0);
        }
        if (productList.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PRODUCT_AVAILABILITY_NOT_FOUND, Boolean.toString(available)));
        }
        return dtoListConverter(productList);
    }

    @Override
    public DtoProduct updateProductById(Long id, DtoProductIU input) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, id.toString())));
        product.setUpdatedAt(new Date());
        product.setName(input.getName());
        product.setDescription(input.getDescription());
        product.setPrice(input.getPrice());
        product.setBrand(input.getBrand());
        product.setCategory(input.getCategory());
        product.setImageUrl(input.getImageUrl());
        product.setStockQuantity(input.getStockQuantity());
        Product updatedProduct = productRepository.save(product);
        return dtoTransformation(updatedProduct);
    }

    @Override
    public void deleteProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.PRODUCT_NOT_FOUND, id.toString())));
        productRepository.delete(product);
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    public Page<Product> findProductsPaged(Pageable pageable) {
        return productRepository.findAllPageable(pageable);
    }

    @Override
    public List<DtoProduct> findByFilter(String brand, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> productList = productRepository.findByFilter(brand, category, minPrice, maxPrice);
        if (productList.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.PRODUCT_FILTER_NOT_FOUND, "Filters: " + brand + ", " + category));
        }
        return dtoListConverter(productList);
    }
}
