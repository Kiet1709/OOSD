package com.example.foodelivery.presentation.restaurant.home.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class RestaurantDashboardEffect : ViewSideEffect {
    object NavigateToLogin : RestaurantDashboardEffect()
}