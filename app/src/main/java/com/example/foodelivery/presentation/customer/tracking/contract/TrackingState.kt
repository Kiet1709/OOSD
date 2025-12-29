package com.example.foodelivery.presentation.customer.tracking.contract

import com.example.foodelivery.core.base.ViewState

enum class TrackingStep(val title: String, val description: String) {
    CONFIRMED("Đã xác nhận", "Nhà hàng đang kiểm tra đơn"),
    KITCHEN("Đang chuẩn bị", "Bếp đang nấu món ngon cho bạn"),
    PICKUP("Tài xế đang đến", "Shipper đang đến lấy hàng"),
    DELIVERING("Đang giao hàng", "Shipper đang trên đường giao"),
    ARRIVED("Đã đến nơi", "Shipper đang đợi bạn ở sảnh")
}

data class DriverUiModel(
    val id: String,
    val name: String,
    val licensePlate: String,
    val phone: String,
    val rating: Double,
    val avatarUrl: String?
)

data class TrackingState(
    val isLoading: Boolean = false,
    val driver: DriverUiModel? = null,
    val currentStep: TrackingStep = TrackingStep.CONFIRMED,
    val eta: String = "--",
    val driverProgress: Float = 0f // 0.0 -> 1.0 (Để vẽ xe chạy trên map)
) : ViewState