package com.zest.product.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * Item entity representing stock/quantity associated with a product.
 * Indexed on product_id for faster lookups.
 */
@Entity
@Table(name = "item", indexes = {
        @Index(name = "idx_item_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Positive(message = "Quantity must be a positive number")
    @Column(nullable = false)
    private Integer quantity;
}
