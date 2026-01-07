package com.example.foodelivery.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val address: String? = null,
    val role: String = "customer" // [QUAN TRỌNG] Đã có trường này
) {
    // Constructor cho Firestore (nếu cần)
    constructor() : this("", "", "", "", "", null, "customer")

    fun isDriver() = role.equals("driver", ignoreCase = true)
    fun isAdmin() = role.equals("admin", ignoreCase = true) || role.equals("store", ignoreCase = true)
}