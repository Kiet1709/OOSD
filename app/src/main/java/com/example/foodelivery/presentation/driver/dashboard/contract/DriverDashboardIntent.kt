package com.example.foodelivery.presentation.driver.dashboard.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class DriverDashboardIntent : ViewIntent {
    object LoadDashboard : DriverDashboardIntent()
    object ToggleOnlineStatus : DriverDashboardIntent() // Bật/Tắt nhận đơn

    data class AcceptOrder(val orderId: String) : DriverDashboardIntent()
    data class RejectOrder(val orderId: String) : DriverDashboardIntent()

    object ClickRevenueDetail : DriverDashboardIntent()
}