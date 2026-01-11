package com.example.foodelivery.presentation.restaurant.home.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState

data class RestaurantDashboardState(
    val restaurantName: String = "",
    val avatarUrl: String = "",
    val todayRevenue: Double = 0.0,
    val totalRevenue: Double = 0.0
) : ViewState


sealed class RestaurantDashboardIntent : ViewIntent {
    object LoadData : RestaurantDashboardIntent() // Add this
    object ClickLogout : RestaurantDashboardIntent()
}

sealed class RestaurantDashboardEffect : ViewSideEffect {
    object NavigateToLogin : RestaurantDashboardEffect()
}
