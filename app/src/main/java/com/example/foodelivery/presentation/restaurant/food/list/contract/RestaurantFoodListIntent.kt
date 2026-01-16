package com.example.foodelivery.presentation.restaurant.food.list.contract

import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.domain.model.Food

sealed class RestaurantFoodListIntent : ViewIntent {
    object LoadFoods : RestaurantFoodListIntent()
    data class ClickDeleteFood(val food: Food) : RestaurantFoodListIntent()
    object ConfirmDeleteFood : RestaurantFoodListIntent()
    object DismissDeleteDialog : RestaurantFoodListIntent()
    object ClickAddFood : RestaurantFoodListIntent()
    data class ClickEditFood(val foodId: String) : RestaurantFoodListIntent()
}