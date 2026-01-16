package com.example.foodelivery.presentation.restaurant.profile.edit.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User

data class RestaurantEditProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val name: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val coverPhotoUrl: String = ""
) : ViewState