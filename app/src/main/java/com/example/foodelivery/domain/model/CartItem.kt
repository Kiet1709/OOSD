package com.example.foodelivery.domain.model

data class CartItem(
    val foodId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val note: String
) {
    val totalPrice: Double get() = price * quantity
}