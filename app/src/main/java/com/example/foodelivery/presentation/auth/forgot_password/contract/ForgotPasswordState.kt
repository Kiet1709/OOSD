package com.example.foodelivery.presentation.auth.forgot_password.contract

import com.example.foodelivery.core.base.ViewState

data class ForgotPasswordState(
    val step: Int = 1, // 1: Nhập Email, 2: Nhập OTP & Mật khẩu mới
    val email: String = "",
    val otpCode: String = "",
    val newPass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPasswordVisible: Boolean = false // Để ẩn/hiện mật khẩu mới
) : ViewState