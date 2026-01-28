package com.example.foodelivery.presentation.driver.dashboard.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class DriverDashboardEffect : ViewSideEffect {
    data class NavigateToDelivery(val orderId: String) : DriverDashboardEffect()
    data class ShowToast(val msg: String) : DriverDashboardEffect()
    object NavigateToProfile : DriverDashboardEffect()
    object NavigateToRevenueReport : DriverDashboardEffect()
    object NavigateToLogin : DriverDashboardEffect()
    object NavigateToChangePassword : DriverDashboardEffect()
}