package com.example.foodelivery.presentation.customer.home.contract

import com.example.foodelivery.core.base.ViewSideEffect

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