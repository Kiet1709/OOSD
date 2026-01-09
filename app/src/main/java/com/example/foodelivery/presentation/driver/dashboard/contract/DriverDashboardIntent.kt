package com.example.foodelivery.presentation.driver.dashboard.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class DriverDashboardIntent : ViewIntent {
    object LoadDashboard : DriverDashboardIntent()
    object ToggleOnlineStatus : DriverDashboardIntent()
    object Refresh : DriverDashboardIntent() // Pull to refresh

    data class AcceptOrder(val orderId: String) : DriverDashboardIntent()
    data class RejectOrder(val orderId: String) : DriverDashboardIntent()

    object ClickRevenueDetail : DriverDashboardIntent()
    object ClickProfile : DriverDashboardIntent()
    object Logout : DriverDashboardIntent() // [MỚI]
}