package com.example.foodelivery.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
data class CartDto(
    @DocumentId val id: String = "",
    val items: List<CartItemDto> = emptyList(),
    val totalPrice: Double = 0.0
)



data class CartItemDto(
    val foodId: String? = null,
    @get:PropertyName("name") @set:PropertyName("name") var name: String? = null,
    val price: Double? = null,
    val quantity: Int? = null,
    val imageUrl: String? = null,
    val note: String? = null
)
