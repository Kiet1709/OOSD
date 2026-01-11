package com.example.foodelivery.presentation.customer.orderhistory.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Order

data class OrderHistoryState(
    val isLoading: Boolean = false,
    val ongoingOrders: List<Order> = emptyList(),
    val completedOrders: List<Order> = emptyList()
) : ViewState
