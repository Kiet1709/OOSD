package com.example.foodelivery.presentation.customer.food.detail.Contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Food

data class FoodDetailState(
    val isLoading: Boolean = true,
    val food: Food? = null,
    val quantity: Int = 1,
    val totalPrice: Double = 0.0
):ViewState