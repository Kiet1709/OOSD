package com.example.foodelivery.presentation.admin.home.contract

import com.example.foodelivery.core.base.ViewSideEffect

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