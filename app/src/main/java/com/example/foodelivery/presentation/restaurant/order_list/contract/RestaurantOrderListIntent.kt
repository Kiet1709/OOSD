package com.example.foodelivery.presentation.restaurant.order_list.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class RestaurantOrderListIntent : ViewIntent {
    data class ChangeOrderStatus(val orderId: String, val newStatus: String) : RestaurantOrderListIntent()
    data class ViewOrderDetail(val orderId: String) : RestaurantOrderListIntent()
}