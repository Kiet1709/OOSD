package com.example.foodelivery.presentation.customer.food.detail.Contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.presentation.customer.home.contract.FoodUiModel
data class FoodDetailState(
    val isLoading: Boolean = true,
    val food: FoodUiModel? = null,
    val quantity: Int = 1,
    val totalPrice: Double = 0.0
):ViewState