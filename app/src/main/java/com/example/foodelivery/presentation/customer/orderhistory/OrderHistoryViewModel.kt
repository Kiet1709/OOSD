package com.example.foodelivery.presentation.customer.orderhistory

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import com.example.foodelivery.presentation.customer.orderhistory.contract.OrderHistoryEffect
import com.example.foodelivery.presentation.customer.orderhistory.contract.OrderHistoryIntent
import com.example.foodelivery.presentation.customer.orderhistory.contract.OrderHistoryState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: IOrderRepository
) : BaseViewModel<OrderHistoryState, OrderHistoryIntent, OrderHistoryEffect>(OrderHistoryState()) {

    init {
        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            orderRepository.getOrderHistory(userId).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val orders = resource.data ?: emptyList()
                        val ongoing = orders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
                        val completed = orders.filter { it.status == OrderStatus.DELIVERED || it.status == OrderStatus.CANCELLED }
                        setState { copy(isLoading = false, ongoingOrders = ongoing, completedOrders = completed) }
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                    }
                    is Resource.Loading -> {
                        setState { copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun setEvent(intent: OrderHistoryIntent) = handleIntent(intent)

    override fun handleIntent(intent: OrderHistoryIntent) {
        when (intent) {
            is OrderHistoryIntent.OnOrderClick -> {
                setEffect { OrderHistoryEffect.NavigateToOrderDetail(intent.orderId) }
            }
        }
    }
}