package com.zest.product.management.mapper;

import com.zest.product.management.dto.ItemDto;
import com.zest.product.management.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "product.id", target = "productId")
    ItemDto toDto(Item item);

    @Mapping(target = "product", ignore = true)
    Item toEntity(ItemDto itemDto);
}
