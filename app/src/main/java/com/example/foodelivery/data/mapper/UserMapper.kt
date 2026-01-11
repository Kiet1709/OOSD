package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.UserEntity
import com.example.foodelivery.data.remote.dto.UserDto
import com.example.foodelivery.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = this.id,
        name = this.name ?: "",
        email = this.email ?: "",
        phoneNumber = this.phoneNumber ?: "",
        role = this.role?.uppercase() ?: "CUSTOMER",
        avatarUrl = this.avatarUrl ?: "",
        coverPhotoUrl = this.coverPhotoUrl ?: "",
        address = this.address
    )
}

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name ?: "",
        email = this.email ?: "",
        phoneNumber = this.phoneNumber,
        avatarUrl = this.avatarUrl,
        coverPhotoUrl = this.coverPhotoUrl,
        address = this.address,
        role = this.role?.uppercase() ?: "CUSTOMER"
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        email = this.email,
        phoneNumber = this.phoneNumber,
        avatarUrl = this.avatarUrl,
        coverPhotoUrl = this.coverPhotoUrl,
        address = this.address,
        role = this.role.uppercase()
    )
}

// Add the missing mappers

fun User.toDto(): UserDto {
    return UserDto(
        id = this.id,
        name = this.name,
        email = this.email,
        phoneNumber = this.phoneNumber,
        avatarUrl = this.avatarUrl,
        coverPhotoUrl = this.coverPhotoUrl,
        address = this.address,
        role = this.role
    )
}

fun UserDto.toDomain(): User {
    return User(
        id = this.id,
        name = this.name ?: "",
        email = this.email ?: "",
        phoneNumber = this.phoneNumber ?: "",
        avatarUrl = this.avatarUrl ?: "",
        coverPhotoUrl = this.coverPhotoUrl ?: "",
        address = this.address,
        role = this.role ?: "CUSTOMER"
    )
}
