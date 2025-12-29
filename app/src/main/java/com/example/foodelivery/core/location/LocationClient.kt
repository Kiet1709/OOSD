package com.example.foodelivery.core.location
import android.location.Location
import kotlinx.coroutines.flow.Flow
interface LocationClient {

    // Trả về Flow để update vị trí liên tục (theo interval)
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String) : Exception(message)
}