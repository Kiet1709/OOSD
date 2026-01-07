package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.UserEntity
import com.example.foodelivery.data.remote.dto.UserDto
import com.example.foodelivery.domain.model.User

// 1. Chuyển từ Entity (DB) -> Domain (App)
fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        name = this.name ?: "",
        email = this.email ?: "",
        phoneNumber = this.phoneNumber ?: "",
        role = this.role ?: "customer", // [QUAN TRỌNG] Lấy role từ DB, mặc định customer

        avatarUrl = this.avatarUrl ?: "",
        address = null
    )
}

// 2. Chuyển từ DTO (API) -> Entity (DB)
fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name ?: "",
        email = this.email ?: "",
        phoneNumber = this.phoneNumber,
        avatarUrl = this.avatarUrl,
        role = this.role ?: "customer" // [QUAN TRỌNG] Map role từ API xuống DB
    )
}

// 3. Chuyển từ Domain (App) -> Entity (DB)
fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        phoneNumber = this.phoneNumber,
        avatarUrl = this.avatarUrl,
        role = this.role // [QUAN TRỌNG] Lưu role từ App vào DB
    )
}