package com.example.foodelivery.presentation.restaurant.order_list.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Order

data class RestaurantOrderListState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList()
) : ViewState