package com.example.foodelivery.presentation.customer.food.list.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class FoodListIntent : ViewIntent {
    data class LoadList(val type: String) : FoodListIntent()
    data class ClickFood(val id: String) : FoodListIntent()
    object ClickBack : FoodListIntent()
}