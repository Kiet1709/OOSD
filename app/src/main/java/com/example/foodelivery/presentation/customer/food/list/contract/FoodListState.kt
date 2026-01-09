package com.example.foodelivery.presentation.customer.food.list.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Food

data class FoodListState(
    val isLoading: Boolean = true,
    val title: String = "",
    val foods: List<Food> = emptyList()
) : ViewState