package com.example.foodelivery.presentation.driver.dashboard

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Constants
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.driver.dashboard.contract.DriverDashboardEffect
import com.example.foodelivery.presentation.driver.dashboard.contract.DriverDashboardIntent
import com.example.foodelivery.presentation.driver.dashboard.contract.DriverDashboardState
import com.example.foodelivery.presentation.driver.dashboard.contract.DriverOrderUiModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverDashboardViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val userRepository: IUserRepository
) : BaseViewModel<DriverDashboardState, DriverDashboardIntent, DriverDashboardEffect>(DriverDashboardState()) {

    private val _isOnline = MutableStateFlow(false)

    override val uiState = combine(
        userRepository.getUser(),
        orderRepository.getAllOrders(),
        _isOnline
    ) { user, ordersResource, isOnline ->
        val availableOrders = if (ordersResource is Resource.Success) {
            ordersResource.data
                ?.filter { it.status == OrderStatus.DELIVERING && it.driverId.isNullOrEmpty() }
                ?.map { order ->
                    val restaurant = userRepository.getUserById(order.restaurantId)
                    DriverOrderUiModel(
                        id = order.id,
                        restaurantName = restaurant?.name ?: "Không rõ",
                        restaurantAddress = restaurant?.address ?: "Không rõ",
                        customerAddress = order.shippingAddress,
                        earning = Constants.DELIVERY_FEE * Constants.DRIVER_EARNING_RATE,
                        timeAgo = "", // Placeholder
                        distanceKm = 0.0 // Placeholder
                    )
                } ?: emptyList()
        } else {
            emptyList()
        }

        DriverDashboardState(
            user = user,
            isLoading = ordersResource is Resource.Loading,
            availableOrders = availableOrders,
            isOnline = isOnline,
            error = if (ordersResource is Resource.Error) ordersResource.message else null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DriverDashboardState(isLoading = true))

    init {
        // Initial load is handled by the stateIn operator
    }

    fun setEvent(intent: DriverDashboardIntent) = handleIntent(intent)

    override fun handleIntent(intent: DriverDashboardIntent) {
        when (intent) {
            is DriverDashboardIntent.AcceptOrder -> acceptOrder(intent.orderId)
            DriverDashboardIntent.ToggleOnlineStatus -> {
                _isOnline.value = !_isOnline.value
            }
            DriverDashboardIntent.ClickChangePassword -> setEffect { DriverDashboardEffect.NavigateToChangePassword }
            DriverDashboardIntent.Logout -> logout()
            DriverDashboardIntent.ClickProfile -> setEffect { DriverDashboardEffect.NavigateToProfile }
            else -> {}
        }
    }

    private fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            val driverId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val result = orderRepository.updateOrderStatus(orderId, OrderStatus.DELIVERING.name, driverId)
            if (result is Resource.Success) {
                setEffect { DriverDashboardEffect.NavigateToDelivery(orderId) }
            } else {
                setEffect { DriverDashboardEffect.ShowToast(result.message ?: "Lỗi nhận đơn") }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            setEffect { DriverDashboardEffect.NavigateToLogin }
        }
    }
}