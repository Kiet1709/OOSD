package com.example.foodelivery.presentation.customer.food.detail.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.model.User

data class FoodDetailState(
    val isLoading: Boolean = false,
    val food: Food? = null,
    val restaurant: User? = null,
    val quantity: Int = 1
) : ViewState

sealed class FoodDetailIntent : ViewIntent {
    data class LoadFoodDetail(val foodId: String) : FoodDetailIntent()
    object IncreaseQuantity : FoodDetailIntent()
    object DecreaseQuantity : FoodDetailIntent()
    object AddToCart : FoodDetailIntent()
}

sealed class FoodDetailEffect : ViewSideEffect {
    data class ShowToast(val message: String) : FoodDetailEffect()
    object NavigateBack : FoodDetailEffect()
}