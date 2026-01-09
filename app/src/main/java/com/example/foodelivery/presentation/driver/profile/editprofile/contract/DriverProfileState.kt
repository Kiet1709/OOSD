package com.example.foodelivery.presentation.driver.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewState

data class DriverEditProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val address: String = "", // Có thể là "Khu vực hoạt động"
    val avatarUrl: String = ""
) : ViewState