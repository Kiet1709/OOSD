package com.example.foodelivery.data.remote.dto
import com.google.firebase.firestore.DocumentId

data class UserDto(
     val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null, // Khớp
    val avatarUrl: String? = null,
    val role: String? = "CUSTOMER"
)