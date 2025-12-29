package com.example.foodelivery.data.remote.dto
import com.google.firebase.firestore.DocumentId

data class FoodDto(
    @DocumentId val id: String = "",
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val imageUrl: String? = null,
    val categoryId: String? = null,
    val rating: Double? = null,
    val isAvailable: Boolean? = true
)