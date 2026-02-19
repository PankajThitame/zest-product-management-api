package com.zest.product.management.service;

import com.zest.product.management.dto.ItemDto;
import com.zest.product.management.entity.Item;
import com.zest.product.management.entity.Product;
import com.zest.product.management.mapper.ItemMapper;
import com.zest.product.management.repository.ItemRepository;
import com.zest.product.management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemService itemService;

    private Product product;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        product = Product.builder().id(1L).build();
        item = Item.builder().id(1L).product(product).quantity(10).build();
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setQuantity(10);
    }

    @Test
    void getItemsByProductId_WhenProductExists_ShouldReturnList() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findByProductId(1L)).thenReturn(Collections.singletonList(item));
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        List<ItemDto> result = itemService.getItemsByProductId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getItemsByProductId_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> itemService.getItemsByProductId(99L));
    }

    @Test
    void addItemToProduct_ShouldSaveItem() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(itemMapper.toEntity(any(ItemDto.class))).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(any(Item.class))).thenReturn(itemDto);

        ItemDto result = itemService.addItemToProduct(1L, itemDto);

        assertNotNull(result);
        verify(itemRepository).save(any(Item.class));
    }
}
