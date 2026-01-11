package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.CartEntity
import com.example.foodelivery.data.remote.dto.CartItemDto
import com.example.foodelivery.domain.model.CartItem

// --- CartEntity (Local DB) to CartItem (Domain) ---
// Now that both classes are non-nullable, the mapping is a direct 1-to-1
fun CartEntity.toDomain(): CartItem {
    return CartItem(
        foodId = this.foodId,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
        imageUrl = this.imageUrl,
        note = this.note,
        restaurantId = this.restaurantId // Add this line
    )
}

// --- CartItem (Domain) to CartEntity (Local DB) ---
fun CartItem.toEntity(): CartEntity {
    return CartEntity(
        foodId = this.foodId,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
        imageUrl = this.imageUrl,
        note = this.note,
        restaurantId = this.restaurantId // Add this line
    )
}

// --- CartItemDto (Firebase) to CartItem (Domain) ---
// Safely handle nullable fields from the network
fun CartItemDto.toDomain(): CartItem {
    return CartItem(
        foodId = this.foodId ?: "",
        name = this.name ?: "",
        price = this.price ?: 0.0,
        quantity = this.quantity ?: 0,
        imageUrl = this.imageUrl ?: "",
        note = this.note ?: "",
        restaurantId = this.restaurantId ?: "" // Add this line
    )
}

// --- CartItem (Domain) to CartItemDto (Firebase) ---
// Prepare data for sending to the network
fun CartItem.toDto(): CartItemDto {
    return CartItemDto(
        foodId = this.foodId,
        name = this.name,
        price = this.price,
        quantity = this.quantity,
        imageUrl = this.imageUrl,
        note = this.note,
        restaurantId = this.restaurantId // Add this line
    )
}
