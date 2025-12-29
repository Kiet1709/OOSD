package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.OrderEntity
import com.example.foodelivery.data.remote.dto.CartItemDto
import com.example.foodelivery.data.remote.dto.OrderDto
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.OrderStatus

fun OrderDto.toDomain() = Order(
    id = id, userId = userId?:"", driverId = driverId,
    status = OrderStatus.fromString(status), totalPrice = totalPrice?:0.0,
    shippingAddress = shippingAddress?:"", timestamp = timestamp?.time?:System.currentTimeMillis(),
    items = items?.map { it.toDomain() } ?: emptyList()
)
fun Order.toDto() = OrderDto(
    id = id, userId = userId, driverId = driverId,
    status = status.value, totalPrice = totalPrice,
    shippingAddress = shippingAddress, timestamp = null, // Server timestamp
    items = items.map { it.toDto() }
)
fun OrderEntity.toDomain() = Order(
    id = id, userId = userId, driverId = driverId,
    status = OrderStatus.fromString(status), totalPrice = totalPrice,
    shippingAddress = shippingAddress, timestamp = timestamp,
    items = emptyList()
)