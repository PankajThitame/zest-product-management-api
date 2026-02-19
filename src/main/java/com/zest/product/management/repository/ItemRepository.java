package com.zest.product.management.repository;

import com.zest.product.management.entity.Item;
import com.zest.product.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByProduct(Product product);

    List<Item> findByProductId(Long productId);
}
