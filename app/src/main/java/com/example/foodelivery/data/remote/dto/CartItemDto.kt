package com.example.foodelivery.data.remote.dto

data class CartItemDto(
    val foodId: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val quantity: Int? = null,
    val imageUrl: String? = null,
    val note: String? = null
)