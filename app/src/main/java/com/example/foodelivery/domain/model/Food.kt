package com.example.foodelivery.domain.model

data class Food(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val categoryId: String,
    val rating: Double,
    val isAvailable: Boolean
){
    // Constructor rỗng cho Firestore
    constructor() : this("", "", "", 0.0, "", "", 0.0, true)
    // Logic phụ trợ: Format giá tiền
    val formattedPrice: String
        get() = String.format("%,.0f đ", price)
}