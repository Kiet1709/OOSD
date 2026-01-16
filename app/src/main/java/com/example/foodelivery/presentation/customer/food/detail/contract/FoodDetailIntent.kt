package com.example.foodelivery.presentation.customer.food.detail.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class FoodDetailIntent : ViewIntent {
    data class LoadFoodDetail(val foodId: String) : FoodDetailIntent()
    object IncreaseQuantity : FoodDetailIntent()
    object DecreaseQuantity : FoodDetailIntent()
    object AddToCart : FoodDetailIntent()
}