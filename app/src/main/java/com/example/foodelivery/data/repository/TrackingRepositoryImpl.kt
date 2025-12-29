package com.example.foodelivery.data.repository

import com.example.foodelivery.presentation.customer.tracking.contract.DriverUiModel
import com.example.foodelivery.presentation.customer.tracking.contract.TrackingStep
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// Interface (Thường nằm ở Domain, nhưng viết gộp ở đây để bạn dễ copy)
interface ITrackingRepository {
    fun trackOrder(orderId: String): Flow<Pair<TrackingStep, Float>> // Trả về Bước & Tiến độ (0.0 -> 1.0)
    suspend fun getDriverInfo(orderId: String): DriverUiModel
}

class TrackingRepositoryImpl @Inject constructor() : ITrackingRepository {

    // Giả lập luồng đơn hàng thay đổi theo thời gian
    override fun trackOrder(orderId: String): Flow<Pair<TrackingStep, Float>> = flow {
        // 1. Đã xác nhận
        emit(TrackingStep.CONFIRMED to 0f)
        delay(3000)

        // 2. Nhà bếp đang nấu
        emit(TrackingStep.KITCHEN to 0f)
        delay(4000)

        // 3. Tài xế đang đến lấy (Xe chạy 50%)
        emit(TrackingStep.PICKUP to 0.5f)
        delay(3000)
        emit(TrackingStep.PICKUP to 1.0f) // Đã lấy hàng
        delay(1000)

        // 4. Đang giao hàng (Mô phỏng xe chạy từ 0 -> 100%)
        var progress = 0f
        while (progress < 1.0f) {
            emit(TrackingStep.DELIVERING to progress)
            progress += 0.2f // Tăng dần tiến độ
            delay(1500)
        }

        // 5. Đã đến nơi
        emit(TrackingStep.ARRIVED to 1.0f)
    }

    override suspend fun getDriverInfo(orderId: String): DriverUiModel {
        delay(500) // Giả lập mạng
        return DriverUiModel(
            id = "DRV-007",
            name = "Nguyễn Văn Tài Xế",
            licensePlate = "59-X1 123.45",
            phone = "0909123456",
            rating = 4.9,
            avatarUrl = "https://randomuser.me/api/portraits/men/32.jpg"
        )
    }
}