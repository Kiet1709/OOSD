package com.example.foodelivery.presentation.customer.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewState

data class EditProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val avatarUrl: String = "",
    val coverPhotoUrl: String = ""
) : ViewState