package com.example.foodelivery.presentation.customer.cart.contract

data class CartItemUiModel(
    val foodId: String, // Khóa chính (trùng với Database)
    val name: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val note: String = "",
    val restaurantId: String // Add this field
) {
    val itemTotal: Double get() = price * quantity
}
