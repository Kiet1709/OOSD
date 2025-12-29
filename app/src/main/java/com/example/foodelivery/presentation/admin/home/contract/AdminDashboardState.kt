package com.example.foodelivery.presentation.admin.home.contract

import com.example.foodelivery.core.base.ViewState

data class AdminDashboardState(
    // Thông tin Admin
    val adminName: String = "Admin",
    val avatarUrl: String? = null,
    val notificationCount: Int = 0,

    // Số liệu thống kê (Business Metrics)
    val todayRevenue: Double = 0.0,
    val totalOrders: Int = 0,
    val pendingOrders: Int = 0, // Đơn chờ xử lý (Critical metric)
    val activeDrivers: Int = 0,

    // Trạng thái Loading
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false // Hỗ trợ kéo để refresh (Pull-to-refresh)
) : ViewState