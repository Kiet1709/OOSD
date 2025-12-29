package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.CartEntity
import com.example.foodelivery.data.remote.dto.CartItemDto
import com.example.foodelivery.domain.model.CartItem

fun CartEntity.toDomain() = CartItem(foodId, name, price, quantity, imageUrl, note?:"")
fun CartItem.toEntity() = CartEntity(foodId, name, price, quantity, imageUrl, note)
fun CartItem.toDto() = CartItemDto(foodId, name, price, quantity, imageUrl, note)
fun CartItemDto.toDomain() = CartItem(foodId?:"", name?:"", price?:0.0, quantity?:1, imageUrl?:"", note?:"")