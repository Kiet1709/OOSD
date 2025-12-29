package com.example.foodelivery.presentation.auth.contract

import com.example.foodelivery.core.base.ViewState

data class LoginState(
    val email: String = "",
    val pass: String = "",
    val isPasswordVisible: Boolean = false, // Trạng thái ẩn/hiện mật khẩu
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
) : ViewState

