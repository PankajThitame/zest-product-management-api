package com.zest.product.management.config;

import com.zest.product.management.entity.Item;
import com.zest.product.management.entity.Product;
import com.zest.product.management.entity.Role;
import com.zest.product.management.repository.ProductRepository;
import com.zest.product.management.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds initial data into the database on startup.
 * Ensures roles and sample products exist for testing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {
        seedRoles();
        seedProducts();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            log.info("Seeding initial security roles (USER, ADMIN)...");
            roleRepository.save(Role.builder().name(Role.RoleName.ROLE_USER).build());
            roleRepository.save(Role.builder().name(Role.RoleName.ROLE_ADMIN).build());
            log.info("Roles seeded successfully.");
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            log.info("Seeding sample products for testing...");

            Product p1 = Product.builder().productName("Gaming Laptop - Alienware M16").build();
            p1.addItem(Item.builder().quantity(10).build());
            productRepository.save(p1);

            Product p2 = Product.builder().productName("Smartphone - Pixel 8 Pro").build();
            p2.addItem(Item.builder().quantity(25).build());
            productRepository.save(p2);

            Product p3 = Product.builder().productName("Noise Cancelling Headphones").build();
            p3.addItem(Item.builder().quantity(50).build());
            productRepository.save(p3);

            log.info("Sample products seeded successfully.");
        }
    }
}
