package com.example.foodelivery.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val driverId: String?,
    val totalPrice: Double,
    val status: String,
    val shippingAddress: String,
    val timestamp: Long
)