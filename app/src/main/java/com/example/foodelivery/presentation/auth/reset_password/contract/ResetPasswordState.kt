package com.example.foodelivery.presentation.auth.reset_password.contract

import com.example.foodelivery.core.base.ViewState

data class ResetPasswordState(
    val newPass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) : ViewState