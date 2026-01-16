package com.example.foodelivery.presentation.restaurant.food.list.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class RestaurantFoodListEffect : ViewSideEffect {
    data class ShowToast(val message: String) : RestaurantFoodListEffect()
    data class NavigateToEditFood(val foodId: String) : RestaurantFoodListEffect()
    object NavigateToAddFood : RestaurantFoodListEffect()
}