package com.example.foodelivery.presentation.admin.home.contract

import com.example.foodelivery.core.base.ViewState

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
