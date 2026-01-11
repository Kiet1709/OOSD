package com.example.foodelivery.presentation.customer.food.list.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class CustomerFoodListIntent : ViewIntent {
    data class LoadFoods(val categoryId: String) : CustomerFoodListIntent()
    data class ClickFood(val foodId: String) : CustomerFoodListIntent()
}
