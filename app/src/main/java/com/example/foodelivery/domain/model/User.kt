package com.example.foodelivery.domain.model

data class User(
    val id: String = "",          // [FIX]: Thêm = ""
    val name: String = "",        // [FIX]: Thêm = ""
    val email: String = "",       // [FIX]: Thêm = ""
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val address: String? = null,
    val role: String = "customer" // [FIX]: Thêm mặc định
) {
    // Constructor rỗng (Bắt buộc phải có để Firestore chạy được)
    constructor() : this("", "", "", "", "", null, "customer")

    fun isDriver() = role == "driver"
    fun isAdmin() = role == "admin"
}