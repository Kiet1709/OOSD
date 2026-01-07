package com.example.foodelivery.data.remote.dto

import com.google.firebase.firestore.DocumentId

data class FoodDto(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val categoryId: String = "",
    val rating: Double = 5.0,
    val isAvailable: Boolean = true
)