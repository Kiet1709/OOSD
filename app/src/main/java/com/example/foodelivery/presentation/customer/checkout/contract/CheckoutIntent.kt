package com.example.foodelivery.presentation.customer.checkout.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class CheckoutIntent : ViewIntent {
    object ConfirmOrder : CheckoutIntent()
}
