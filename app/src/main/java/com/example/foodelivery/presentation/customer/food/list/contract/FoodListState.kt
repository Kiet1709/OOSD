package com.example.foodelivery.presentation.customer.food.list.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.presentation.customer.home.contract.FoodUiModel

data class FoodListState(
    val isLoading: Boolean = true,
    val title: String = "",
    val foods: List<FoodUiModel> = emptyList()
) : ViewState