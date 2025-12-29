package com.example.foodelivery.domain.repository

import kotlinx.coroutines.flow.Flow

interface IDriverRepository {
    fun getDriverLocation(driverId: String): Flow<Pair<Double, Double>>
    suspend fun updateMyLocation(driverId: String, lat: Double, lng: Double, bearing: Float)
}