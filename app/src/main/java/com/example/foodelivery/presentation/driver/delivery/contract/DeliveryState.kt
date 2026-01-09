package com.example.foodelivery.presentation.driver.delivery.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User

// --- ENUM: Quy trình giao hàng ---
enum class DeliveryStep(val buttonText: String, val instruction: String) {
    HEADING_TO_RESTAURANT("ĐÃ ĐẾN QUÁN", "Di chuyển đến nhà hàng"),
    PICKING_UP("ĐÃ LẤY MÓN", "Nhận món và kiểm tra"),
    DELIVERING("ĐÃ ĐẾN NƠI", "Di chuyển đến khách hàng"),
    ARRIVED("HOÀN TẤT ĐƠN HÀNG", "Giao tận tay khách")
}

// --- MODEL ---
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

// --- STATE ---
data class DeliveryState(
    val isLoading: Boolean = false,
    val order: DeliveryOrderInfo? = null,
    val customer: User? = null,
    val currentStep: DeliveryStep = DeliveryStep.HEADING_TO_RESTAURANT,
    val mapProgress: Float = 0f // 0f -> 1f (Để vẽ xe chạy trên map)
) : ViewState