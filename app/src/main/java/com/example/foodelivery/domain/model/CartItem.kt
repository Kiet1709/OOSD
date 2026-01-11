package com.example.foodelivery.domain.model

data class CartItem(
    val foodId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val note: String,
    val restaurantId: String // Add this field
) {
    val totalPrice: Double get() = price * quantity

    fun isValid(): Boolean {
        return foodId.isNotEmpty() &&
                name.isNotEmpty() &&
                price > 0 &&
                quantity > 0 &&
                imageUrl.isNotEmpty()
    }
}