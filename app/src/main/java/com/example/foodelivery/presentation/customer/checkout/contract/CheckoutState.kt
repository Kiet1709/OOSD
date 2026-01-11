package com.example.foodelivery.presentation.customer.checkout.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.presentation.customer.cart.contract.CartItemUiModel

data class CheckoutState(
    val isLoading: Boolean = false,
    val items: List<CartItemUiModel> = emptyList(),
    val address: String = "",
    val subTotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val finalTotal: Double = 0.0
) : ViewState
