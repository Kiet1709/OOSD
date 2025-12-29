package com.example.foodelivery.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class CategoryDto(
    @DocumentId val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    @get:PropertyName("isActive") @set:PropertyName("isActive")
    var isActive: Boolean = true
)
