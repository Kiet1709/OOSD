package com.example.foodelivery.data.remote.dto

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserDto(
    @DocumentId val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val avatarUrl: String? = null,
    val coverPhotoUrl: String? = null, // New
    val address: String? = null,
    val role: String? = "CUSTOMER"
)