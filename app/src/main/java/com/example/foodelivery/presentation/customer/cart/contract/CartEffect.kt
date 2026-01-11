package com.example.foodelivery.presentation.customer.cart.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class CartEffect : ViewSideEffect {
    object NavigateToHome : CartEffect()
    object NavigateToCheckout : CartEffect() // No more parameters needed
    data class NavigateToTracking(val orderId: String) : CartEffect()
    data class ShowToast(val msg: String) : CartEffect()
}
