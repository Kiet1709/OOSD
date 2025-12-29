package com.example.foodelivery.presentation.customer.cart.contract

import com.example.foodelivery.core.base.ViewSideEffect


sealed class CartEffect : ViewSideEffect {
    object NavigateToCheckout : CartEffect() // Sang màn thanh toán
    object NavigateToHome : CartEffect()
    data class ShowToast(val msg: String) : CartEffect()
}