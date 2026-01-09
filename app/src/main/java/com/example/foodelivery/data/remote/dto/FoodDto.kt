package com.example.foodelivery.data.remote.dto
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName // [1] Nhớ Import cái này
data class FoodDto(
    @DocumentId val id: String = "",
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val imageUrl: String? = null,
    val categoryId: String? = null,
    val rating: Double? = null,
    @get:PropertyName("isAvailable")
    val isAvailable: Boolean? = true
)