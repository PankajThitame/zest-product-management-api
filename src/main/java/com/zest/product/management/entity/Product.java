package com.zest.product.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Product entity representing the main product record.
 * Includes indexing on productName for performance and uniqueness constraints.
 */
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "product_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(name = "product_name", nullable = false, unique = true)
    private String productName;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    // Helper method for bi-directional relationship management
    public void addItem(Item item) {
        items.add(item);
        item.setProduct(this);
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.setProduct(null);
    }
}
