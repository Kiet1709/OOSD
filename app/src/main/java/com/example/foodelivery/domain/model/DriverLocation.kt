package com.example.foodelivery.domain.model

data class DriverLocation(
    val driverId: String,
    val latitude: Double,
    val longitude: Double,
    val bearing: Float
)