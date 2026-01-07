package com.example.foodelivery.presentation.auth.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class LoginEffect : ViewSideEffect {
    data class ShowToast(val message: String) : LoginEffect()

    sealed class Navigation : LoginEffect() {
        // [MỚI] Điều hướng sang đăng ký, mang theo role đã chọn
        data class ToRegister(val preSelectedRole: String) : Navigation()
        
        object ToForgotPassword : Navigation()
        object ToCustomerHome : Navigation()
        object ToAdminDashboard : Navigation()
        object ToDriverDashboard : Navigation()
    }
}