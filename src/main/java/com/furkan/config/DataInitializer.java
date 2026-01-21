package com.furkan.config;

import com.furkan.enums.Role;
import com.furkan.model.Product;
import com.furkan.model.User;
import com.furkan.repository.OrderRepository;
import com.furkan.repository.ProductRepository;
import com.furkan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DataInitializer(UserRepository userRepository, ProductRepository productRepository,
                           OrderRepository orderRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return;
        }

        if (userRepository.findByEmail("admin@ecommerce.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@ecommerce.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("seller@ecommerce.com").isEmpty()) {
            User seller = new User();
            seller.setUsername("seller");
            seller.setEmail("seller@ecommerce.com");
            seller.setPassword(passwordEncoder.encode("seller123"));
            seller.setRole(Role.SELLER);
            userRepository.save(seller);

            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setSeller(seller);
                p1.setName("Iphone 15 Pro");
                p1.setDescription("Perfect titanium design.");
                p1.setCategory("Phone");
                p1.setBrand("Apple");
                p1.setStockQuantity(50);
                p1.setPrice(new BigDecimal("72000.00"));
                p1.setImageUrl("https://store.storeimages.cdn-apple.com/4668/as-images.apple.com/is/iphone-15-pro-finish-select-202309-6-1inch-naturaltitanium?wid=5120&hei=2880&fmt=p-jpg&qlt=80&.v=1692846360609");

                Product p2 = new Product();
                p2.setName("MacBook Air M3");
                p2.setDescription("İnce, hafif ve çok güçlü.");
                p2.setPrice(new BigDecimal("45000.00"));
                p2.setCategory("Laptop");
                p2.setBrand("Apple");
                p2.setStockQuantity(10);
                p2.setImageUrl("https://www.apple.com/v/macbook-air-13-and-15-m2/e/images/overview/design/design_top_midnight__f98v9qivshm6_large.jpg");

                productRepository.saveAll(List.of(p1, p2));
            }
        }
    }
}
