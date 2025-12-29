package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.CategoryEntity
import com.example.foodelivery.data.remote.dto.CategoryDto
import com.example.foodelivery.domain.model.Category

// Entity -> Domain
fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    imageUrl = imageUrl
)

// Dto -> Entity
fun CategoryDto.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name ?: "",
    imageUrl = imageUrl ?: ""
)

// Dto -> Domain (Load trực tiếp nếu không qua Cache)
fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = name ?: "",
    imageUrl = imageUrl ?: ""
)