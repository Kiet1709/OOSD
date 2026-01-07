package com.example.foodelivery.presentation.auth.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class LoginIntent : ViewIntent {
    data class EmailChanged(val email: String) : LoginIntent()
    data class PasswordChanged(val pass: String) : LoginIntent()
    object TogglePasswordVisibility : LoginIntent()
    object SubmitLogin : LoginIntent()
    object ClickRegister : LoginIntent()
    object ClickForgotPassword : LoginIntent()

    // Intent chuyển đổi chế độ đăng nhập (Customer / Driver / Store)
    data class SwitchLoginMode(val role: String) : LoginIntent()
}