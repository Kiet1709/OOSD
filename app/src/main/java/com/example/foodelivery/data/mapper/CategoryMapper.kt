package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.CategoryEntity
import com.example.foodelivery.data.remote.dto.CategoryDto
import com.example.foodelivery.domain.model.Category

fun CategoryDto.toDomain() = Category(id = id, name = name, imageUrl = imageUrl)
fun CategoryDto.toEntity() = CategoryEntity(id = id, name = name, imageUrl = imageUrl)
fun CategoryEntity.toDomainModel() = Category(id = id, name = name, imageUrl = imageUrl)
fun Category.toEntity() = CategoryEntity(id = id, name = name, imageUrl = imageUrl)
fun Category.toDto() = CategoryDto(id = id, name = name, imageUrl = imageUrl)