package com.example.foodelivery.data.remote.dto
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class OrderDto(
    @DocumentId val id: String = "",
    val userId: String? = null,
    val driverId: String? = null,
    val restaurantId: String? = null, // Add this field
    val status: String? = "PENDING",
    val totalPrice: Double? = null,
    val shippingAddress: String? = null, // Khớp
    @ServerTimestamp val timestamp: Date? = null,
    val items: List<CartItemDto>? = null
)
