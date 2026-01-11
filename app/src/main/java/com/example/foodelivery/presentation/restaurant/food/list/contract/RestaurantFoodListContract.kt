package com.example.foodelivery.presentation.restaurant.food.list.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Food

data class RestaurantFoodListState(
    val isLoading: Boolean = false,
    val foods: List<Food> = emptyList(),
    val foodToDelete: Food? = null
) : ViewState

sealed class RestaurantFoodListIntent : ViewIntent {
    object LoadFoods : RestaurantFoodListIntent()
    data class ClickDeleteFood(val food: Food) : RestaurantFoodListIntent()
    object ConfirmDeleteFood : RestaurantFoodListIntent()
    object DismissDeleteDialog : RestaurantFoodListIntent()
    object ClickAddFood : RestaurantFoodListIntent()
    data class ClickEditFood(val foodId: String) : RestaurantFoodListIntent()
}

sealed class RestaurantFoodListEffect : ViewSideEffect {
    data class ShowToast(val message: String) : RestaurantFoodListEffect()
    data class NavigateToEditFood(val foodId: String) : RestaurantFoodListEffect()
    object NavigateToAddFood : RestaurantFoodListEffect()
}