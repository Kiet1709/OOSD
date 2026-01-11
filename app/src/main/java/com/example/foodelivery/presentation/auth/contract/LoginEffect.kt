package com.example.foodelivery.presentation.auth.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class LoginEffect : ViewSideEffect {
    data class ShowToast(val message: String) : LoginEffect()


    sealed class Navigation : LoginEffect() {
        object ToRegister : Navigation()
        object ToForgotPassword : Navigation()
        object ToCustomerHome : Navigation()
        object ToAdminDashboard : Navigation()
        object ToDriverDashboard : Navigation()
        object ToRestaurantDashboard : Navigation() // New
    }
}
