package com.example.foodelivery.presentation.customer.checkout.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class CheckoutEffect : ViewSideEffect {
    object NavigateToHome : CheckoutEffect() // Add this
    data class NavigateToTracking(val orderId: String) : CheckoutEffect()
    data class ShowToast(val message: String) : CheckoutEffect()
}
