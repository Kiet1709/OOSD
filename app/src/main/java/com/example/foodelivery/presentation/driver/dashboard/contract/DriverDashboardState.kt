package com.example.foodelivery.presentation.driver.dashboard.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User


data class DriverDashboardState(
    val isLoading: Boolean = false,
    val isOnline: Boolean = false,
    val todayRevenue: Double = 0.0,
    val availableOrders: List<DriverOrderUiModel> = emptyList(),
    val error: String? = null,
    val user: User? = null
) : ViewState
