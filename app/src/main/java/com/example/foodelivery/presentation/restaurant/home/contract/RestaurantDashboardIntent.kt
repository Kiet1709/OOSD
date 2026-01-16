package com.example.foodelivery.presentation.restaurant.home.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class RestaurantDashboardIntent : ViewIntent {
    object LoadData : RestaurantDashboardIntent() // Add this
    object ClickLogout : RestaurantDashboardIntent()
}