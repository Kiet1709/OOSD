package com.example.foodelivery.presentation.customer.orderdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IOrderRepository
import com.example.foodelivery.presentation.customer.orderdetail.contract.OrderDetailEffect
import com.example.foodelivery.presentation.customer.orderdetail.contract.OrderDetailIntent
import com.example.foodelivery.presentation.customer.orderdetail.contract.OrderDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<OrderDetailState, OrderDetailIntent, OrderDetailEffect>(OrderDetailState()) {

    init {
        savedStateHandle.get<String>("orderId")?.let {
            loadOrderDetail(it)
        }
    }

    private fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            when (val result = orderRepository.getOrderDetail(orderId)) {
                is Resource.Success -> {
                    setState { copy(isLoading = false, order = result.data) }
                }
                is Resource.Error -> {
                    setState { copy(isLoading = false) }
                    // Handle error
                }
                else -> {}
            }
        }
    }

    override fun handleIntent(intent: OrderDetailIntent) {
        // Handle intents if any
    }
}