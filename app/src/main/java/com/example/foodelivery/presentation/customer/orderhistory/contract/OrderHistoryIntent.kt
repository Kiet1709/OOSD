package com.example.foodelivery.presentation.customer.orderhistory.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class OrderHistoryIntent : ViewIntent {
    data class OnOrderClick(val orderId: String) : OrderHistoryIntent()
}