package com.example.foodelivery.presentation.customer.home.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class CustomerHomeIntent : ViewIntent {
    object LoadHomeData : CustomerHomeIntent()
    object Refresh : CustomerHomeIntent() // Kéo để refresh

    // Actions
    data class ClickFood(val foodId: String) : CustomerHomeIntent()
    data class ClickCategory(val categoryId: String) : CustomerHomeIntent()
    object ClickCart : CustomerHomeIntent()
    object ClickSearch : CustomerHomeIntent()
    object ClickProfile : CustomerHomeIntent() // Bấm vào Avatar
    object ClickSettings : CustomerHomeIntent()
    object ClickLogout : CustomerHomeIntent()
}