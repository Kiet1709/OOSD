package com.example.foodelivery.presentation.driver.dashboard.contract

data class DriverOrderUiModel(
    val id: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val customerAddress: String,
    val earning: Double,
    val timeAgo: String,
    val distanceKm: Double
)
