package com.example.foodelivery.presentation.restaurant.profile.view.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User

data class RestaurantProfileState(
    val isLoading: Boolean = false,
    val user: User? = null
) : ViewState