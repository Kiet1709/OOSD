package com.example.foodelivery.presentation.customer.home.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.model.User

data class CustomerHomeState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val categories: List<CategoryUiModel> = emptyList(),
    val foods: List<Food> = emptyList()
) : ViewState