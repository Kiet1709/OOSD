package com.example.foodelivery.presentation.admin.home.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class AdminDashboardIntent : ViewIntent {
    object LoadData : AdminDashboardIntent()
    object RefreshData : AdminDashboardIntent()

    // Header Actions
    object ClickLogout : AdminDashboardIntent()
    object ClickProfile : AdminDashboardIntent()
    object ClickSettings : AdminDashboardIntent()

    // Menu Actions
    object ClickManageOrders : AdminDashboardIntent()
    object ClickManageFood : AdminDashboardIntent()
    object ClickManageCategory : AdminDashboardIntent() // Thêm Category
    object ClickManageDrivers : AdminDashboardIntent()
    object ClickManageUsers : AdminDashboardIntent()
    object ClickPromotions : AdminDashboardIntent()
    object ClickReviews : AdminDashboardIntent()
    object ClickReports : AdminDashboardIntent()
}