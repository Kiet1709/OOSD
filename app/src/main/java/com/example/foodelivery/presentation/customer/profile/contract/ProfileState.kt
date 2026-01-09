package com.example.foodelivery.presentation.customer.profile.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val appVersion: String = "1.0.0"
) : ViewState