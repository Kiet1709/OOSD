package com.example.foodelivery.data.remote.dto
import com.example.foodelivery.domain.model.User
import com.google.firebase.firestore.DocumentId

data class UserDto(
     val id: String = "",
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null, // Khớp
    val avatarUrl: String? = null,
     val address: String? = null, // [THÊM DÒNG NÀY]
    val role: String? = "CUSTOMER"
) {
    // Thêm hàm này để chuyển đổi từ DTO (Firebase) sang Domain (App dùng)
    fun toDomain(): User {
        return User(
            id = this.id,
            name = this.name ?: "",
            email = this.email ?: "",
            phoneNumber = this.phoneNumber ?: "",
            avatarUrl = this.avatarUrl ?: "",
            address = this.address ?: "", // Map địa chỉ sang
            role = this.role ?: "CUSTOMER"
        )
    }
}