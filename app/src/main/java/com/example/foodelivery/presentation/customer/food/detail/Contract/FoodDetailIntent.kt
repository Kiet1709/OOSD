package com.example.foodelivery.presentation.customer.food.detail.Contract

import com.example.foodelivery.core.base.ViewIntent

sealed class FoodDetailIntent :ViewIntent {
    data class LoadDetail(val foodId: String) : FoodDetailIntent()
    object ClickBack : FoodDetailIntent()
    object IncreaseQuantity : FoodDetailIntent()
    object DecreaseQuantity : FoodDetailIntent()
    object ClickAddToCart : FoodDetailIntent()
}