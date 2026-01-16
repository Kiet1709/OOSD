package com.example.foodelivery.presentation.customer.home.contract

import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.domain.model.Food

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
