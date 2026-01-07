package com.example.foodelivery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val avatarUrl: String?,
    val role: String? // [QUAN TRỌNG] Đã có trường này
)