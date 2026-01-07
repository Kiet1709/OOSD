package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.FoodEntity
import com.example.foodelivery.data.remote.dto.FoodDto
import com.example.foodelivery.domain.model.Food

// Entity -> Domain
fun FoodEntity.toDomainModel(): Food {
    return Food(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        categoryId = categoryId,
        rating = rating,
        isAvailable = isAvailable
    )
}

// Domain -> Entity
fun Food.toEntity(): FoodEntity {
    return FoodEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        categoryId = categoryId,
        rating = rating,
        isAvailable = isAvailable
    )
}

// Dto (Remote) -> Entity (Local)
fun FoodDto.toEntity(): FoodEntity {
    return FoodEntity(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        categoryId = categoryId,
        rating = rating,
        isAvailable = isAvailable
    )
}

// Domain -> Dto (Remote)
fun Food.toDto(): FoodDto {
    return FoodDto(
        id = id,
        name = name,
        description = description,
        price = price,
        imageUrl = imageUrl,
        categoryId = categoryId,
        rating = rating,
        isAvailable = isAvailable
    )
}