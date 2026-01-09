package com.example.foodelivery.presentation.driver.delivery

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IUserRepository // [MỚI] Dùng UserRepo
import com.example.foodelivery.domain.usecase.driver.GetOrderDetailUseCase
import com.example.foodelivery.domain.usecase.order.UpdateOrderStatusUseCase
import com.example.foodelivery.presentation.driver.delivery.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverDeliveryViewModel @Inject constructor(
    private val getOrderDetailUseCase: GetOrderDetailUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val userRepository: IUserRepository // Inject UserRepo riêng biệt
) : BaseViewModel<DeliveryState, DeliveryIntent, DeliveryEffect>(DeliveryState()) {

    private var simulationJob: Job? = null
    private var currentOrderId: String = ""

    fun setEvent(intent: DeliveryIntent) = handleIntent(intent)

    override fun handleIntent(intent: DeliveryIntent) {
        when(intent) {
            is DeliveryIntent.LoadOrder -> {
                currentOrderId = intent.orderId
                loadRealOrderData(intent.orderId)
            }
            DeliveryIntent.ClickMainAction -> handleNextStep()
            DeliveryIntent.ClickCallCustomer -> {
                // Lấy SĐT từ object Customer trong State
                val phone = currentState.customer?.phoneNumber ?: ""
                if (phone.isNotEmpty()) setEffect { DeliveryEffect.OpenDialer(phone) }
                else setEffect { DeliveryEffect.ShowToast("Khách chưa cập nhật số điện thoại") }
            }
            DeliveryIntent.ClickMapNavigation -> setEffect { DeliveryEffect.ShowToast("Đang mở Google Maps...") }
            DeliveryIntent.ClickChatCustomer -> setEffect { DeliveryEffect.ShowToast("Chat với ${currentState.customer?.name}") }
        }
    }

    private fun loadRealOrderData(orderId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // 1. Lấy Order (đã map sang Domain)
            val result = getOrderDetailUseCase(orderId)

            if (result is Resource.Success) {
                val order = result.data!! // Đây là Order (Domain Model)

                // 2. Lấy User (Khách hàng) từ UserRepository
                val customer = userRepository.getUserById(order.userId)

                // 3. Map sang UI Info (thông tin hiển thị)
                val orderInfo = DeliveryOrderInfo(
                    id = order.id,
                    restaurantName =  "Cửa hàng đối tác",
                    restaurantAddress = "Khu vực trung tâm",
                    customerName = customer?.name ?: "Khách hàng",
                    customerPhone = customer?.phoneNumber ?: "",
                    customerAddress = order.shippingAddress,
                    totalAmount = order.totalPrice ,
                )

                // 4. Đồng bộ trạng thái từ Domain Enum -> UI Step
                val step = when(order.status) {
                    OrderStatus.CONFIRMED ->
                        if (currentState.currentStep == DeliveryStep.PICKING_UP) DeliveryStep.PICKING_UP
                        else DeliveryStep.HEADING_TO_RESTAURANT
                    OrderStatus.DELIVERING -> DeliveryStep.DELIVERING
                    OrderStatus.DELIVERED -> DeliveryStep.ARRIVED
                    else -> DeliveryStep.HEADING_TO_RESTAURANT
                }

                // 5. Cập nhật State
                setState {
                    copy(
                        isLoading = false,
                        order = orderInfo,
                        customer = customer, // Lưu thông tin khách vào state
                        currentStep = step
                    )
                }

                if (step == DeliveryStep.DELIVERING) startMapSimulation()
                else simulationJob?.cancel()

            } else {
                setState { copy(isLoading = false) }
                setEffect { DeliveryEffect.ShowToast(result.message ?: "Lỗi tải đơn hàng") }
            }
        }
    }

    private fun handleNextStep() {
        val currentStep = currentState.currentStep
        when (currentStep) {
            DeliveryStep.HEADING_TO_RESTAURANT -> setState { copy(currentStep = DeliveryStep.PICKING_UP) }
            DeliveryStep.PICKING_UP -> updateFirebaseStatus(OrderStatus.DELIVERING.name)
            DeliveryStep.DELIVERING -> updateFirebaseStatus(OrderStatus.DELIVERED.name)
            DeliveryStep.ARRIVED -> setEffect { DeliveryEffect.NavigateBackDashboard }
        }
    }

    private fun updateFirebaseStatus(status: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = updateOrderStatusUseCase(currentOrderId, status)

            if (result is Resource.Success) {
                setEffect { DeliveryEffect.ShowToast("Đã cập nhật trạng thái!") }
                if (status == OrderStatus.DELIVERED.name) {
                    setState { copy(isLoading = false, currentStep = DeliveryStep.ARRIVED) }
                    simulationJob?.cancel()
                } else if (status == OrderStatus.DELIVERING.name) {
                    setState { copy(isLoading = false, currentStep = DeliveryStep.DELIVERING) }
                    startMapSimulation()
                }
            } else {
                setEffect { DeliveryEffect.ShowToast("Lỗi: ${result.message}") }
                setState { copy(isLoading = false) }
            }
        }
    }

    private fun startMapSimulation() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            var p = 0f
            while (p <= 1f) {
                setState { copy(mapProgress = p) }
                p += 0.005f
                delay(50)
            }
        }
    }
}