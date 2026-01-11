package com.example.foodelivery.presentation.customer.orderdetail.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Order

data class OrderDetailState(
    val isLoading: Boolean = false,
    val order: Order? = null
) : ViewState
