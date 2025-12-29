package com.example.foodelivery.core.common

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationHelper {
    // Lấy vị trí hiện tại (1 lần) - Dùng khi đặt hàng
    suspend fun getCurrentLocation(): Location?

    // Theo dõi vị trí (Liên tục) - Dùng cho Tài xế
    fun getLocationFlow(intervalMs: Long = 5000L): Flow<Location>
}