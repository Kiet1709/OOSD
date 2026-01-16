package com.example.foodelivery.presentation.driver.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewState

data class DriverEditProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val avatarUrl: String = ""
) : ViewState