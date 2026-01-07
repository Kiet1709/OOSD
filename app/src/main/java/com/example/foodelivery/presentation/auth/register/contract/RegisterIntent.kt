package com.example.foodelivery.presentation.auth.register.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class RegisterIntent : ViewIntent {
    data class NameChanged(val name: String) : RegisterIntent()
    data class EmailChanged(val email: String) : RegisterIntent()
    data class PhoneChanged(val phone: String) : RegisterIntent()
    data class PasswordChanged(val password: String) : RegisterIntent()
    data class ConfirmPasswordChanged(val confirmPass: String) : RegisterIntent()
    
    object TogglePasswordVisibility : RegisterIntent()
    object ToggleConfirmPasswordVisibility : RegisterIntent()
    
    object SubmitRegister : RegisterIntent()
    object ClickLogin : RegisterIntent()

    data class SelectRole(val role: String) : RegisterIntent()
}