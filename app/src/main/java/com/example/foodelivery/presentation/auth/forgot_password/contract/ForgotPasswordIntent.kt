package com.example.foodelivery.presentation.auth.forgot_password.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class ForgotPasswordIntent : ViewIntent {
    // Step 1: Input
    data class EmailChanged(val value: String) : ForgotPasswordIntent()
    object ClickSendOtp : ForgotPasswordIntent() // Chuyển từ bước 1 -> 2

    // Step 2: Input
    data class OtpChanged(val value: String) : ForgotPasswordIntent()
    data class NewPassChanged(val value: String) : ForgotPasswordIntent()
    object TogglePasswordVisibility : ForgotPasswordIntent()
    object ClickSubmitReset : ForgotPasswordIntent() // Xác nhận đổi pass

    // Navigation
    object ClickBackToLogin : ForgotPasswordIntent()
}