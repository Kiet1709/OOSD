package com.example.foodelivery.presentation.restaurant.order_list.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Order

data class RestaurantOrderListState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList()
) : ViewState

sealed class RestaurantOrderListIntent : ViewIntent {
    data class ChangeOrderStatus(val orderId: String, val newStatus: String) : RestaurantOrderListIntent()
    data class ViewOrderDetail(val orderId: String) : RestaurantOrderListIntent()
}

sealed class RestaurantOrderListEffect : ViewSideEffect {
    data class NavigateToOrderDetail(val orderId: String) : RestaurantOrderListEffect()
    data class ShowToast(val message: String) : RestaurantOrderListEffect()
}
