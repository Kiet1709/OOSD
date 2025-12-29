package com.example.foodelivery.domain.model
import com.example.foodelivery.domain.model.OrderStatus


// 2. Data Class chứa thông tin đơn hàng
data class TrackingOrder(
    val id: String,
    val status: OrderStatus,
    val estimatedTime: String, // VD: "15-20 phút"
    val driverName: String? = null,
    val driverPhone: String? = null,
    val driverAvatar: String? = null,
    val currentLat: Double = 0.0,
    val currentLng: Double = 0.0
)