package com.example.foodelivery.presentation.driver.delivery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.driver.delivery.contract.* // Import các file contract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class DriverDeliveryViewModel @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val userRepository: IUserRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<DeliveryState, DeliveryIntent, DeliveryEffect>(DeliveryState()) {

    private val orderId = savedStateHandle.get<String>("orderId")
    private var simulationJob: Job? = null

    fun setEvent(intent: DeliveryIntent) {
        handleIntent(intent)
    }

    init {
        if (orderId != null) {
            handleIntent(DeliveryIntent.LoadOrder(orderId))
        }
    }

    override fun handleIntent(intent: DeliveryIntent) {
        when (intent) {
            is DeliveryIntent.LoadOrder -> loadOrderDetails(intent.orderId)
            is DeliveryIntent.ClickMainAction -> handleMainAction()
            is DeliveryIntent.MarkAsDelivered -> updateOrderStatus(OrderStatus.DELIVERED.name)
            is DeliveryIntent.ClickCallCustomer -> {
                val phone = uiState.value.customer?.phoneNumber  ?: ""
                if (phone.isNotEmpty()) setEffect { DeliveryEffect.OpenDialer(phone) }
                else setEffect { DeliveryEffect.ShowToast("Khách chưa cập nhật SĐT") }
            }
            is DeliveryIntent.ClickChatCustomer -> setEffect { DeliveryEffect.ShowToast("Tính năng Chat đang phát triển") }
            is DeliveryIntent.ClickMapNavigation -> setEffect { DeliveryEffect.ShowToast("Đang mở Google Maps...") }
        }
    }

    private fun handleMainAction() {
        val currentStep = uiState.value.currentStep

        when(currentStep) {
            DeliveryStep.HEADING_TO_RESTAURANT -> {
                setState { copy(currentStep = DeliveryStep.PICKING_UP, mapProgress = 0.4f) }
            }
            DeliveryStep.PICKING_UP -> {
                updateOrderStatus(OrderStatus.DELIVERING.name)
                setState { copy(currentStep = DeliveryStep.DELIVERING, mapProgress = 0.7f) }
            }
            DeliveryStep.DELIVERING -> {
                setState { copy(currentStep = DeliveryStep.ARRIVED, mapProgress = 1.0f) }
            }
            DeliveryStep.ARRIVED -> {
                updateOrderStatus(OrderStatus.DELIVERED.name)
                setEffect { DeliveryEffect.NavigateBackDashboard }
            }
        }
    }

    private fun updateOrderStatus(status: String) {
        if (orderId == null) return
        viewModelScope.launch {
            val result = orderRepository.updateOrderStatus(orderId, status, null)
            if (result is Resource.Error) {
                setEffect { DeliveryEffect.ShowToast(result.message ?: "Lỗi cập nhật") }
            }
        }
    }

    private fun loadOrderDetails(id: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val orderResult = orderRepository.getOrderDetail(id)

            if (orderResult is Resource.Success && orderResult.data != null) {
                val order = orderResult.data!!
                val customerDeferred = async { userRepository.getUserById(order.userId) }
                val restaurantDeferred = async { userRepository.getUserById(order.restaurantId) }

                val customer = customerDeferred.await()
                val restaurant = restaurantDeferred.await()

                setState {
                    copy(
                        isLoading = false,
                        order = order,
                        customer = customer,
                        restaurant = restaurant,
                        currentStep = DeliveryStep.HEADING_TO_RESTAURANT,
                        mapProgress = 0.1f
                    )
                }
            } else {
                setState { copy(isLoading = false) }
                setEffect { DeliveryEffect.ShowToast("Lỗi tải đơn hàng") }
            }
        }
    }
    private fun markAsDelivered() {
        if (orderId == null) return
        viewModelScope.launch {
            val result = orderRepository.updateOrderStatus(orderId, OrderStatus.DELIVERED.name, null)
            if (result is Resource.Success) {
                setEffect { DeliveryEffect.NavigateBackDashboard }
            } else {
                setEffect { DeliveryEffect.ShowToast(result.message ?: "Lỗi cập nhật") }
            }
        }
    }
    private fun startMapSimulation() {
        simulationJob?.cancel()

        simulationJob = viewModelScope.launch {
            var p = 0f
            while (p <= 1f) {
                setState { copy(mapProgress = p) } // Cập nhật vị trí xe
                p += 0.005f
                delay(50)
            }
        }
    }

    private fun stopMapSimulation() {
        simulationJob?.cancel()
    }
}