package com.example.foodelivery.presentation.driver.delivery.contract

data class DeliveryOrderInfo(
    val id: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val totalAmount: Double, // Số tiền thu hộ (nếu có)
    val note: String = ""
)