package com.example.foodelivery.presentation.driver.dashboard.contract

import com.example.foodelivery.core.base.ViewState

// Model cho đơn hàng hiển thị phía Tài xế
data class DriverOrderUiModel(
    val id: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val customerAddress: String,
    val distanceKm: Double,
    val earning: Double, // Số tiền tài xế nhận được
    val timeAgo: String // VD: "vừa xong", "2 phút trước"
)

data class DriverDashboardState(
    val isLoading: Boolean = false,
    val isOnline: Boolean = false, // Trạng thái Online/Offline
    val todayRevenue: Double = 0.0, // Doanh thu hôm nay

    // Danh sách đơn hàng mới (Pending)
    val availableOrders: List<DriverOrderUiModel> = emptyList()
) : ViewState