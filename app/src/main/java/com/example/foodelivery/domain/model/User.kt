package com.example.foodelivery.domain.model

import com.google.firebase.firestore.Exclude

data class User(
    // Annotate with @get:Exclude to prevent this field from being sent to Firestore
    @get:Exclude
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val coverPhotoUrl: String = "",
    val address: String? = null,
    val role: String = "CUSTOMER"
) {
    constructor() : this("", "", "", "", "", "", null, "CUSTOMER")

    fun isDriver() = role.equals("DRIVER", ignoreCase = true)
    fun isAdmin() = role.equals("ADMIN", ignoreCase = true)
    fun isRestaurant() = role.equals("RESTAURANT", ignoreCase = true)
}