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
        role = this.role ?: "user",

        avatarUrl = this.avatarUrl ?: "",
        address = null // Hoặc this.address nếu Entity có trường này
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
        role = this.role ?: "customer"
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
        role = this.role
    )
}