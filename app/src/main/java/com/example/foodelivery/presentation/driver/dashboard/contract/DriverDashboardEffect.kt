package com.example.foodelivery.presentation.driver.dashboard.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class DriverDashboardEffect : ViewSideEffect {
    data class NavigateToDelivery(val orderId: String) : DriverDashboardEffect() // Chuyển sang màn đi giao
    object NavigateToRevenueReport : DriverDashboardEffect()
    data class ShowToast(val msg: String) : DriverDashboardEffect()
}