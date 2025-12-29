package com.example.foodelivery.presentation.admin.order.list.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class OrderListEffect : ViewSideEffect {
    data class ShowToast(val message: String) : OrderListEffect()
    data class NavigateToDetail(val orderId: String) : OrderListEffect()
}