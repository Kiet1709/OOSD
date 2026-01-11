package com.example.foodelivery.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties // Tells Firestore to ignore any extra fields in the document
data class FoodDto(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val categoryId: String = "",
    val restaurantId: String = "",
    val rating: Double = 0.0,
    @get:PropertyName("available")
    val isAvailable: Boolean = true
)