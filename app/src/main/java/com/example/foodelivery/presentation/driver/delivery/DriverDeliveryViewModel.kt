package com.example.foodelivery.presentation.driver.delivery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.driver.delivery.contract.DriverDeliveryEffect
import com.example.foodelivery.presentation.driver.delivery.contract.DriverDeliveryIntent
import com.example.foodelivery.presentation.driver.delivery.contract.DriverDeliveryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverDeliveryViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val userRepository: IUserRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<DriverDeliveryState, DriverDeliveryIntent, DriverDeliveryEffect>(DriverDeliveryState()) {

    private val orderId = savedStateHandle.get<String>("orderId")

    init {
        loadOrderDetails()
    }

    fun setEvent(intent: DriverDeliveryIntent) = handleIntent(intent)

    override fun handleIntent(intent: DriverDeliveryIntent) {
        when (intent) {
            DriverDeliveryIntent.MarkAsDelivered -> markAsDelivered()
        }
    }

    private fun loadOrderDetails() {
        if (orderId == null) return
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val orderResult = orderRepository.getOrderDetail(orderId)
            if (orderResult is Resource.Success) {
                val order = orderResult.data
                if (order != null) {
                    val customerDeferred = async { userRepository.getUserById(order.userId) }
                    val restaurantDeferred = async { userRepository.getUserById(order.restaurantId) }
                    val customer = customerDeferred.await()
                    val restaurant = restaurantDeferred.await()
                    setState {
                        copy(
                            isLoading = false,
                            order = order,
                            customer = customer,
                            restaurant = restaurant
                        )
                    }
                } else {
                    setState { copy(isLoading = false) }
                }
            } else {
                setState { copy(isLoading = false) }
            }
        }
    }

    private fun markAsDelivered() {
        if (orderId == null) return
        viewModelScope.launch {
            val result = orderRepository.updateOrderStatus(orderId, OrderStatus.DELIVERED.name, null)
            if (result is Resource.Success) {
                setEffect { DriverDeliveryEffect.NavigateBack }
            } else {
                setEffect { DriverDeliveryEffect.ShowToast(result.message ?: "Lỗi cập nhật") }
            }
        }
    }
}