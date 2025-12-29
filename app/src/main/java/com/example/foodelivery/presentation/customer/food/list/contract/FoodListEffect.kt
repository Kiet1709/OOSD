package com.example.foodelivery.presentation.customer.food.list.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class FoodListEffect : ViewSideEffect {
    object NavigateBack : FoodListEffect()
    data class NavigateToDetail(val id: String) : FoodListEffect()
    data class ShowToast(val msg: String) : FoodListEffect()
}