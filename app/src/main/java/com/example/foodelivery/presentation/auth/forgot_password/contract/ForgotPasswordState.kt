package com.example.foodelivery.presentation.auth.forgot_password.contract

import com.example.foodelivery.core.base.ViewState

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) : ViewState