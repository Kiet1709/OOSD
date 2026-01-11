package com.example.foodelivery.domain.model

import androidx.compose.ui.graphics.Color

fun OrderStatus.toVietnamese(): String {
    return when (this) {
        OrderStatus.PENDING -> "Đang chờ"
        OrderStatus.CONFIRMED -> "Đã xác nhận"
        OrderStatus.PREPARING -> "Đang chuẩn bị"
        OrderStatus.DELIVERING -> "Đang giao"
        OrderStatus.DELIVERED -> "Đã giao"
        OrderStatus.CANCELLED -> "Đã hủy"
        else -> "Không rõ"
    }
}

fun OrderStatus.toColor(): Color {
    return when (this) {
        OrderStatus.PENDING -> Color(0xFFFFA500) // Orange
        OrderStatus.CONFIRMED -> Color(0xFF1E90FF) // DodgerBlue
        OrderStatus.PREPARING -> Color(0xFF8A2BE2) // BlueViolet
        OrderStatus.DELIVERING -> Color(0xFF32CD32) // LimeGreen
        OrderStatus.DELIVERED -> Color.Gray
        OrderStatus.CANCELLED -> Color.Red
        else -> Color.Black
    }
}
