package com.zest.product.management.mapper;

import com.zest.product.management.dto.ProductDto;
import com.zest.product.management.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto toDto(Product product);

    @Mapping(target = "items", ignore = true)
    Product toEntity(ProductDto productDto);
}
