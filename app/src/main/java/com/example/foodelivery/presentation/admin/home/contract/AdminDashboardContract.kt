package com.example.foodelivery.presentation.admin.home.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState

// --- STATE ---
data class AdminDashboardState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val adminName: String = "",
    val avatarUrl: String = "",
    val notificationCount: Int = 0,
    val todayRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val pendingOrders: Int = 0,
    val activeDrivers: Int = 0
) : ViewState

// --- INTENT ---
sealed class AdminDashboardIntent : ViewIntent {
    object LoadData : AdminDashboardIntent()
    object RefreshData : AdminDashboardIntent()
    object ClickLogout : AdminDashboardIntent()
    object ClickProfile : AdminDashboardIntent()
    object ClickSettings : AdminDashboardIntent()

    // New Menu Intents
    object ClickManageUsers : AdminDashboardIntent()
    object ClickManageDrivers : AdminDashboardIntent()
    object ClickManageRestaurants : AdminDashboardIntent()
    object ClickManageCategory : AdminDashboardIntent()
}

// --- EFFECT ---
sealed class AdminDashboardEffect : ViewSideEffect {
    data class ShowToast(val message: String) : AdminDashboardEffect()
    object NavigateToLogin : AdminDashboardEffect()
    object NavigateToProfile : AdminDashboardEffect()

    // New Menu Navigation Effects
    object NavigateToManageUsers : AdminDashboardEffect()
    object NavigateToManageDrivers : AdminDashboardEffect()
    object NavigateToManageRestaurants : AdminDashboardEffect()
    object NavigateToCategoryList : AdminDashboardEffect()
}