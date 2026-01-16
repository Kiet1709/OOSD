package com.example.foodelivery.presentation.restaurant.food.list.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Food

data class RestaurantFoodListState(
    val isLoading: Boolean = false,
    val foods: List<Food> = emptyList(),
    val foodToDelete: Food? = null
) : ViewState