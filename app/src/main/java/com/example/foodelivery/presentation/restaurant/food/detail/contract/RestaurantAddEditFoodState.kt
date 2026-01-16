package com.example.foodelivery.presentation.restaurant.food.detail.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Category

data class RestaurantAddEditFoodState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val foodId: String? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val categories: List<Category> = emptyList(),
    val nameError: String? = null,
    val priceError: String? = null
) : ViewState