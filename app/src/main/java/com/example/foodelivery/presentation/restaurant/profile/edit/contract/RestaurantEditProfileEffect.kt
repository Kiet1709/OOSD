package com.example.foodelivery.presentation.restaurant.profile.edit.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class RestaurantEditProfileEffect : ViewSideEffect {
    data class ShowToast(val message: String) : RestaurantEditProfileEffect()
    object NavigateBack : RestaurantEditProfileEffect()
}