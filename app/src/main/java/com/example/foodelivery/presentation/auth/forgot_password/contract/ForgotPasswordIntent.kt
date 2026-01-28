package com.example.foodelivery.presentation.auth.forgot_password.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class ForgotPasswordIntent : ViewIntent {
    data class EmailChanged(val value: String) : ForgotPasswordIntent()
    object ClickSendLink : ForgotPasswordIntent()
    object ClickBackToLogin : ForgotPasswordIntent()
}