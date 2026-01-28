package com.example.foodelivery.presentation.auth.reset_password.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class ResetPasswordIntent : ViewIntent {
    data class PassChanged(val value: String) : ResetPasswordIntent()
    object ClickSubmit : ResetPasswordIntent()
}