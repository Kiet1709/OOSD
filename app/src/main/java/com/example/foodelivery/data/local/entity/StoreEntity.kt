package com.example.foodelivery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foodelivery.domain.model.StoreInfo

@Entity(tableName = "store_info")
data class StoreEntity(
    @PrimaryKey val id: Int = 1, // Luôn là 1 vì chỉ có 1 cửa hàng
    val name: String,
    val address: String,
    val phoneNumber: String,
    val description: String,
    val avatarUrl: String,
    val coverUrl: String
)

// Extension Mapper
fun StoreEntity.toDomain() = StoreInfo(
    name = name,
    address = address,
    phoneNumber = phoneNumber,
    description = description,
    avatarUrl = avatarUrl,
    coverUrl = coverUrl
)

fun StoreInfo.toEntity() = StoreEntity(
    id = 1,
    name = name,
    address = address,
    phoneNumber = phoneNumber,
    description = description,
    avatarUrl = avatarUrl,
    coverUrl = coverUrl
)