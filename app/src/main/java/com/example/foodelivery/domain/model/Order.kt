package com.example.foodelivery.domain.model

import java.io.Serializable

data class Order(
    val id: String,
    val userId: String,
    val driverId: String?,
    val restaurantId: String, // Add this field
    val status: OrderStatus,
    val totalPrice: Double,
    val shippingAddress: String, // Chuẩn hóa
    val timestamp: Long,         // Dùng Long cho dễ xử lý
    val items: List<CartItem>
): Serializable {

    // ===== Computed Properties =====
    val itemCount: Int get() = items.sumOf { it.quantity }

    val isCompleted: Boolean get() = status == OrderStatus.DELIVERED

    val isCancelled: Boolean get() = status == OrderStatus.CANCELLED

    val canBeCancelled: Boolean get() =
        status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED

    val isDelivering: Boolean get() = status == OrderStatus.DELIVERING

    // ===== Validation =====
    fun isValid(): Boolean {
        return id.isNotEmpty() &&
                userId.isNotEmpty() &&
                restaurantId.isNotEmpty() && // Add this check
                status != OrderStatus.PENDING || shippingAddress.isNotEmpty() &&
                totalPrice > 0 &&
                items.isNotEmpty()
    }
}