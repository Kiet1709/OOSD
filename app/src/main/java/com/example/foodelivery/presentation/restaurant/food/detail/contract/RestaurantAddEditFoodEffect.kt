package com.example.foodelivery.presentation.restaurant.food.detail.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class RestaurantAddEditFoodEffect : ViewSideEffect {
    data class ShowToast(val message: String) : RestaurantAddEditFoodEffect()
    object NavigateBack : RestaurantAddEditFoodEffect()
}