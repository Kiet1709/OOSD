package com.example.foodelivery.presentation.customer.food.list.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class CustomerFoodListEffect : ViewSideEffect {
    data class NavigateToFoodDetail(val foodId: String) : CustomerFoodListEffect()
    data class ShowToast(val message: String) : CustomerFoodListEffect()
}
