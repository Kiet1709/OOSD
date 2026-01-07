package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.CartEntity
import com.example.foodelivery.data.remote.dto.CartItemDto
import com.example.foodelivery.domain.model.CartItem

fun CartEntity.toDomain() = CartItem(
    foodId = foodId,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    note = note
)

fun CartItem.toEntity() = CartEntity(
    foodId = foodId,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    note = note
)

fun CartItem.toDto() = CartItemDto(
    foodId = foodId,
    name = name,
    price = price,
    quantity = quantity,
    imageUrl = imageUrl,
    note = note
)

fun CartItemDto.toDomain() = CartItem(
    foodId = foodId ?: "",
    name = name ?: "",
    price = price ?: 0.0,
    quantity = quantity ?: 1,
    imageUrl = imageUrl ?: "",
    note = note ?: ""
)