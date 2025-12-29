package com.example.foodelivery.data.local.entity
import androidx.room.Entity;
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val driverId: String?,
    val status: String,
    val totalPrice: Double,
    val shippingAddress: String,
    val timestamp: Long
)