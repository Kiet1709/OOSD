package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.FoodEntity
import com.example.foodelivery.data.remote.dto.FoodDto
import com.example.foodelivery.domain.model.Food

fun FoodEntity.toDomain(): Food = Food(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    restaurantId = restaurantId,
    rating = rating,
    isAvailable = isAvailable
)

fun FoodDto.toEntity(): FoodEntity = FoodEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    restaurantId = restaurantId,
    rating = rating,
    isAvailable = isAvailable
)

fun Food.toDto(): FoodDto = FoodDto(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    restaurantId = restaurantId,
    rating = rating,
    isAvailable = isAvailable
)

fun Food.toEntity(): FoodEntity = FoodEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    restaurantId = restaurantId,
    rating = rating,
    isAvailable = isAvailable
)

fun FoodEntity.toDto(): FoodDto = FoodDto(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    restaurantId = restaurantId,
    rating = rating,
    isAvailable = isAvailable
)

fun FoodDto.toDomain(): Food = Food(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    restaurantId = restaurantId,
    rating = rating,
    isAvailable = isAvailable
)