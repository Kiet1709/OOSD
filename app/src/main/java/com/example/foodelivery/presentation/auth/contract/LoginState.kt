package com.example.foodelivery.presentation.auth.contract

import com.example.foodelivery.core.base.ViewState

data class LoginState(
    val isLoading: Boolean = false,
    val email: String = "",
    val pass: String = "",
    val isPasswordVisible: Boolean = false,
    
    // [MỚI] Lưu trạng thái đang đăng nhập với tư cách gì (Mặc định là CUSTOMER)
    val selectedRole: String = "CUSTOMER", 

    val emailError: String? = null,
    val passwordError: String? = null
) : ViewState