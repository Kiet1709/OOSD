package com.example.foodelivery.presentation.admin.home.contract

import com.example.foodelivery.core.base.ViewIntent

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