package com.example.foodelivery.domain.model

data class Food(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val categoryId: String = "",
    val restaurantId: String = "",
    val rating: Double = 0.0,
    val isAvailable: Boolean = true
){
    // Logic phụ trợ: Format giá tiền
    val formattedPrice: String
        get() = String.format("%,.0f đ", price)
}