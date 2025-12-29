package com.example.foodelivery.data.local.entity
import androidx.room.Entity;
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val foodId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val note: String?
)