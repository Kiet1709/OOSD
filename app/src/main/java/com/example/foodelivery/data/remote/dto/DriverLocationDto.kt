package com.example.foodelivery.data.remote.dto

import com.google.firebase.firestore.DocumentId

data class DriverLocationDto(
    @DocumentId val driverId: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val bearing: Float = 0.0f
)
