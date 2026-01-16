package com.example.foodelivery.presentation.restaurant.order_list.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class RestaurantOrderListEffect : ViewSideEffect {
    data class NavigateToOrderDetail(val orderId: String) : RestaurantOrderListEffect()
    data class ShowToast(val message: String) : RestaurantOrderListEffect()
}