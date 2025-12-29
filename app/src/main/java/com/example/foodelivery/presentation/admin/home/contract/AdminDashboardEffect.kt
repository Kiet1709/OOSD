package com.example.foodelivery.presentation.admin.home.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class AdminDashboardEffect : ViewSideEffect {
    data class ShowToast(val message: String) : AdminDashboardEffect()

    // Auth
    object NavigateToLogin : AdminDashboardEffect()

    // Feature Navigation
    object NavigateToAddFood : AdminDashboardEffect()
    object NavigateToManageOrders : AdminDashboardEffect() // Sang màn Order List
    object NavigateToFoodList : AdminDashboardEffect()
    object NavigateToCategoryList : AdminDashboardEffect()

    // Các tính năng chưa phát triển (Placeholder)
    object NavigateToProfile : AdminDashboardEffect()
}