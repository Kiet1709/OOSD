package com.example.foodelivery.presentation.customer.orderhistory.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class OrderHistoryEffect : ViewSideEffect {
    data class NavigateToOrderDetail(val orderId: String) : OrderHistoryEffect()
}