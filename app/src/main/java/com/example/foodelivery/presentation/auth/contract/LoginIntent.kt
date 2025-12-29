package com.example.foodelivery.presentation.auth.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class LoginIntent : ViewIntent {
    data class EmailChanged(val email: String) : LoginIntent()
    data class PasswordChanged(val pass: String) : LoginIntent()
    object TogglePasswordVisibility : LoginIntent() // Bấm icon mắt
    object SubmitLogin : LoginIntent()
    object ClickRegister : LoginIntent()
    object ClickForgotPassword : LoginIntent()

    object ClickSkipLogin : LoginIntent() // <--- MỚI: Khách vãng lai
}

