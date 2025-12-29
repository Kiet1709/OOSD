package com.example.foodelivery.presentation.auth.register.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class RegisterEffect : ViewSideEffect {
    // Hiển thị Toast thông báo
    data class ShowToast(val message: String) : RegisterEffect()

    // Gom nhóm điều hướng vào sealed class Navigation
    sealed class Navigation : RegisterEffect() {
        object ToLogin : Navigation()
        object ToHome : Navigation()
    }
}