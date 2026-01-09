package com.example.foodelivery.presentation.driver.dashboard.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User

// Model cho đơn hàng hiển thị phía Tài xế
data class DriverOrderUiModel(
    val id: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val customerAddress: String,
    val earning: Double,
    val timeAgo: String,
    val distanceKm: Double
)

data class DriverDashboardState(
    val isLoading: Boolean = false,
    val isOnline: Boolean = false, // Trạng thái Online/Offline
    val todayRevenue: Double = 0.0, // Doanh thu hôm nay
    // Danh sách đơn hàng mới (Pending)
    val availableOrders: List<DriverOrderUiModel> = emptyList(),
    val error: String? = null  ,// Lỗi nếu có
    val user: User? = null
) : ViewState