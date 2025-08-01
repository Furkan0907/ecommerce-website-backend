package com.furkan.controller.impl;

import com.furkan.controller.IRestProductController;
import com.furkan.controller.RestBaseController;
import com.furkan.dto.request.DtoProductIU;
import com.furkan.dto.response.DtoProduct;
import com.furkan.model.Product;
import com.furkan.service.IProductService;
import com.furkan.utils.RestPageableEntity;
import com.furkan.utils.RestPageableRequest;
import com.furkan.utils.RootEntity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class RestProductControllerImpl extends RestBaseController implements IRestProductController {

    @Autowired
    private IProductService productService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @PostMapping()
    @Override
    public RootEntity<DtoProduct> saveProduct(@Valid @RequestBody DtoProductIU input) {
        return ok(productService.saveProduct(input));
    }

    @GetMapping("/{id}")
    @Override
    public RootEntity<DtoProduct> findProductById(@PathVariable(value = "id") Long id) {
        return ok(productService.findProductById(id));
    }

    @PreAuthorize("permitAll()")
    @GetMapping()
    @Override
    public RootEntity<List<DtoProduct>> findAllProducts() {
        return ok(productService.findAllProducts());
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/search/by-name")
    @Override
    public RootEntity<DtoProduct> findProductByName(@RequestParam String name) {
        return ok(productService.findProductByName(name));
    }

    @GetMapping("/search/by-brand")
    @Override
    public RootEntity<List<DtoProduct>> findProductsByBrand(@RequestParam String brand) {
        return ok(productService.findProductsByBrand(brand));
    }

    @GetMapping("/search/by-category")
    @Override
    public RootEntity<List<DtoProduct>> findProductsByCategory(@RequestParam String category) {
        return ok(productService.findProductsByCategory(category));
    }

    @GetMapping("/search/by-price")
    @Override
    public RootEntity<List<DtoProduct>> findProductsByPriceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return ok(productService.findProductsByPriceRange(min, max));
    }

    @GetMapping("/search/by-availability")
    @Override
    public RootEntity<List<DtoProduct>> findProductsByAvailability(@RequestParam(required = false, defaultValue = "true") boolean available) {
        return ok(productService.findProductsByAvailability(available));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @PutMapping("/{id}")
    @Override
    public RootEntity<DtoProduct> updateProductById(@PathVariable(value = "id") Long id, @Valid @RequestBody DtoProductIU input) {
        return ok(productService.updateProductById(id, input));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Override
    public RootEntity<Void> deleteProductById(@PathVariable(value = "id") Long id) {
        productService.deleteProductById(id);
        return ok();
    }

    @GetMapping("/count")
    @Override
    public RootEntity<Long> countProducts() {
        return ok(productService.countProducts());
    }

    @GetMapping("/paged")
    @Override
    public RootEntity<RestPageableEntity<DtoProduct>> findProductsPaged(@ModelAttribute RestPageableRequest pageableRequest) {
        Page<Product> page = productService.findProductsPaged(toPageable(pageableRequest));
        List<DtoProduct> content = productService.dtoListConverter(page.getContent());
        RestPageableEntity<DtoProduct> pageableResponse = toPageableResponse(page, content);
        return ok(pageableResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filter")
    @Override
    public RootEntity<List<DtoProduct>> findByFilter(@RequestParam(required = false) String brand,
                                                     @RequestParam(required = false) String category,
                                                     @RequestParam(required = false) BigDecimal minPrice,
                                                     @RequestParam(required = false) BigDecimal maxPrice) {
        return ok(productService.findByFilter(brand, category, minPrice, maxPrice));
    }
}
