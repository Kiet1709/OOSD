package com.example.foodelivery.presentation.restaurant.order_list

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import com.example.foodelivery.domain.usecase.restaurant.UpdateOrderStatusUseCase
import com.example.foodelivery.presentation.restaurant.order_list.contract.RestaurantOrderListEffect
import com.example.foodelivery.presentation.restaurant.order_list.contract.RestaurantOrderListIntent
import com.example.foodelivery.presentation.restaurant.order_list.contract.RestaurantOrderListState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantOrderListViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
) : BaseViewModel<RestaurantOrderListState, RestaurantOrderListIntent, RestaurantOrderListEffect>(RestaurantOrderListState()) {

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            setState { copy(isLoading = true) }
            orderRepository.getAllOrders().collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val filteredOrders = resource.data?.filter { it.restaurantId == uid } ?: emptyList()
                        setState { copy(isLoading = false, orders = filteredOrders) }
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect { RestaurantOrderListEffect.ShowToast(resource.message ?: "Lỗi tải đơn hàng") }
                    }
                    else -> {}
                }
            }
        }
    }

    fun setEvent(intent: RestaurantOrderListIntent) = handleIntent(intent)

    override fun handleIntent(intent: RestaurantOrderListIntent) {
        when (intent) {
            is RestaurantOrderListIntent.ViewOrderDetail -> {
                setEffect { RestaurantOrderListEffect.NavigateToOrderDetail(intent.orderId) }
            }
            is RestaurantOrderListIntent.ChangeOrderStatus -> {
                updateOrderStatus(intent.orderId, intent.newStatus)
            }
        }
    }

    private fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            val status = OrderStatus.valueOf(newStatus.uppercase())
            updateOrderStatusUseCase(orderId, status)
        }
    }
}