package com.zest.product.management.controller;

import com.zest.product.management.dto.ItemDto;
import com.zest.product.management.dto.ProductDto;
import com.zest.product.management.service.ItemService;
import com.zest.product.management.service.ProductService;
import com.zest.product.management.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Product and Item operations.
 * Base path: /api/v1/products
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Endpoints for managing products and their associated items")
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {

    private final ProductService productService;
    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "Get all products with pagination and sorting")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAllProducts(Pageable pageable) {
        return ResponseEntity
                .ok(ApiResponse.success(productService.getAllProducts(pageable), "Products fetched successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single product by ID")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        return ResponseEntity
                .ok(ApiResponse.success(productService.getProductById(id), "Product fetched successfully"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new product (ADMIN only)")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity
                .ok(ApiResponse.success(productService.createProduct(productDto), "Product created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing product (ADMIN only)")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(@PathVariable Long id,
            @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity
                .ok(ApiResponse.success(productService.updateProduct(id, productDto), "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a product (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Get all items belonging to a specific product")
    public ResponseEntity<ApiResponse<List<ItemDto>>> getItemsByProductId(@PathVariable Long id) {
        return ResponseEntity
                .ok(ApiResponse.success(itemService.getItemsByProductId(id), "Items fetched successfully"));
    }

    @PostMapping("/{id}/items")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add an item/quantity to a product (ADMIN only)")
    public ResponseEntity<ApiResponse<ItemDto>> addItemToProduct(@PathVariable Long id,
            @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity
                .ok(ApiResponse.success(itemService.addItemToProduct(id, itemDto), "Item added successfully"));
    }

    @GetMapping("/export-simulation")
    @Operation(summary = "Simulate a heavy async export operation")
    public ResponseEntity<ApiResponse<String>> exportSimulation() {
        productService.simulateSlowExport();
        return ResponseEntity.accepted().body(ApiResponse.success("Export started in background", "Accepted"));
    }
}
