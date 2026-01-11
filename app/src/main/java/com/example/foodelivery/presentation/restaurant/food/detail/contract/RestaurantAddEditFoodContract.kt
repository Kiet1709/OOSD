package com.example.foodelivery.presentation.restaurant.food.detail.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
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

sealed class RestaurantAddEditFoodIntent : ViewIntent {
    data class LoadFoodDetails(val foodId: String) : RestaurantAddEditFoodIntent()
    data class NameChanged(val name: String) : RestaurantAddEditFoodIntent()
    data class DescriptionChanged(val description: String) : RestaurantAddEditFoodIntent()
    data class PriceChanged(val price: String) : RestaurantAddEditFoodIntent()
    data class ImageUrlChanged(val imageUrl: String) : RestaurantAddEditFoodIntent()
    data class CategorySelected(val categoryId: String) : RestaurantAddEditFoodIntent()
    object Submit : RestaurantAddEditFoodIntent()
}

sealed class RestaurantAddEditFoodEffect : ViewSideEffect {
    data class ShowToast(val message: String) : RestaurantAddEditFoodEffect()
    object NavigateBack : RestaurantAddEditFoodEffect()
}