package com.example.foodelivery.data.remote.dto

import com.google.firebase.firestore.DocumentId
data class CartDto(
    @DocumentId val id: String = "",
    val items: List<CartItemDto> = emptyList(),
    val totalPrice: Double = 0.0
)




