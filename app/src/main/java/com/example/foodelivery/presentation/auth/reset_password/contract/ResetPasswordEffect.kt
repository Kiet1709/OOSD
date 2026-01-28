package com.example.foodelivery.presentation.auth.reset_password.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class ResetPasswordEffect : ViewSideEffect {
    data class ShowToast(val message: String) : ResetPasswordEffect()
    object NavigateToLogin : ResetPasswordEffect()
}