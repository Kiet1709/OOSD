package com.example.foodelivery.presentation.auth.forgot_password.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class ForgotPasswordEffect : ViewSideEffect {
    data class ShowToast(val message: String) : ForgotPasswordEffect()
    object NavigateToLogin : ForgotPasswordEffect()
}