package com.example.foodelivery.presentation.admin.food.list.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class FoodListEffect : ViewSideEffect {
    data class ShowToast(val message: String) : FoodListEffect()

    // Điều hướng
    object NavigateToAddScreen : FoodListEffect()
    data class NavigateToEditScreen(val foodId: String) : FoodListEffect()
}