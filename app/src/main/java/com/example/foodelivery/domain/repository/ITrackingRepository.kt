package com.example.foodelivery.domain.repository

import com.example.foodelivery.domain.model.TrackingOrder
import kotlinx.coroutines.flow.Flow

interface ITrackingRepository {
    // Trả về Flow để UI tự cập nhật khi trạng thái thay đổi
    fun trackOrder(orderId: String): Flow<TrackingOrder>
}