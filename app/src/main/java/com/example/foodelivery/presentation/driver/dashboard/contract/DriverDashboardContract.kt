package com.example.foodelivery.presentation.driver.dashboard.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User

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
    val isOnline: Boolean = false,
    val todayRevenue: Double = 0.0,
    val availableOrders: List<DriverOrderUiModel> = emptyList(),
    val error: String? = null,
    val user: User? = null
) : ViewState

sealed class DriverDashboardIntent : ViewIntent {
    object LoadDashboard : DriverDashboardIntent()
    object ToggleOnlineStatus : DriverDashboardIntent()
    object Refresh : DriverDashboardIntent()
    data class AcceptOrder(val orderId: String) : DriverDashboardIntent()
    data class RejectOrder(val orderId: String) : DriverDashboardIntent()
    object ClickRevenueDetail : DriverDashboardIntent()
    object ClickProfile : DriverDashboardIntent()
    object Logout : DriverDashboardIntent()
}

sealed class DriverDashboardEffect : ViewSideEffect {
    data class NavigateToDelivery(val orderId: String) : DriverDashboardEffect()
    data class ShowToast(val msg: String) : DriverDashboardEffect()
    object NavigateToProfile : DriverDashboardEffect()
    object NavigateToRevenueReport : DriverDashboardEffect()
    object NavigateToLogin : DriverDashboardEffect()
}
