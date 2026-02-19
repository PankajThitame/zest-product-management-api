package com.zest.product.management.service;

import com.zest.product.management.dto.ItemDto;
import com.zest.product.management.entity.Item;
import com.zest.product.management.entity.Product;
import com.zest.product.management.exception.ResourceNotFoundException;
import com.zest.product.management.mapper.ItemMapper;
import com.zest.product.management.repository.ItemRepository;
import com.zest.product.management.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to handle Item business logic.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final ItemMapper itemMapper;

    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByProductId(Long productId) {
        log.info("Fetching items for product id: {}", productId);
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        return itemRepository.findByProductId(productId).stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemDto addItemToProduct(Long productId, ItemDto itemDto) {
        log.info("Adding item to product id: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Item item = itemMapper.toEntity(itemDto);
        item.setProduct(product);
        return itemMapper.toDto(itemRepository.save(item));
    }
}
