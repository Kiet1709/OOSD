package com.example.foodelivery.presentation.driver.profile.change_password.contract

import com.example.foodelivery.core.base.ViewState

data class ChangePasswordState(
    val currentPass: String = "",
    val newPass: String = "",
    val confirmPass: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) : ViewState