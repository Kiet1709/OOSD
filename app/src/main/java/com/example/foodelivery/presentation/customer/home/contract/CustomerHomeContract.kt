package com.example.foodelivery.presentation.customer.home.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.model.User

// 1. Updated State: Replaced popular/recommended with a single list
data class CustomerHomeState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val categories: List<CategoryUiModel> = emptyList(),
    val foods: List<Food> = emptyList()
) : ViewState

sealed class CustomerHomeIntent : ViewIntent {
    data class ClickFood(val food: Food) : CustomerHomeIntent()
    data class ClickCategory(val categoryId: String) : CustomerHomeIntent()
    // These can be removed or repurposed later if not needed
    object ClickViewAllPopular : CustomerHomeIntent()
    object ClickViewAllRecommended : CustomerHomeIntent()
    object ClickCart : CustomerHomeIntent()
    object ClickProfile : CustomerHomeIntent()
    object ClickCurrentOrder : CustomerHomeIntent()
    object ClickSearch: CustomerHomeIntent()
    object ClickSettings: CustomerHomeIntent()
    object ClickLogout: CustomerHomeIntent()
}

sealed class CustomerHomeEffect : ViewSideEffect {
    data class NavigateToFoodDetail(val foodId: String) : CustomerHomeEffect()
    data class NavigateToCategory(val categoryId: String) : CustomerHomeEffect()
    data class NavigateToFoodList(val type: String): CustomerHomeEffect()
    object NavigateToCart : CustomerHomeEffect()
    object NavigateToProfile : CustomerHomeEffect()
    object NavigateToSettings : CustomerHomeEffect()
    object NavigateToLogin : CustomerHomeEffect()
    object NavigateToOrderHistory : CustomerHomeEffect() // Add this
    data class NavigateToTracking(val orderId: String): CustomerHomeEffect()
    data class ShowToast(val msg: String) : CustomerHomeEffect()
}

data class CategoryUiModel(
    val id: String,
    val name: String,
    val iconUrl: String
)