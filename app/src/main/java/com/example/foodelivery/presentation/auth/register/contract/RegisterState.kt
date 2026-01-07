package com.example.foodelivery.presentation.auth.register.contract

import com.example.foodelivery.core.base.ViewState

data class RegisterState(
    val isLoading: Boolean = false,

    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirmPass: String = "",
    
    val role: String = "CUSTOMER", 

    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmPassError: String? = null
) : ViewState