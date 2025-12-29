package com.example.foodelivery.presentation.driver.delivery

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.presentation.driver.delivery.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverDeliveryViewModel @Inject constructor() :
    BaseViewModel<DeliveryState, DeliveryIntent, DeliveryEffect>(DeliveryState()) {

    private var simulationJob: Job? = null

    // Hàm public để UI gọi
    fun setEvent(intent: DeliveryIntent) = handleIntent(intent)

    override fun handleIntent(intent: DeliveryIntent) {
        when(intent) {
            is DeliveryIntent.LoadOrder -> loadOrderData(intent.orderId)

            DeliveryIntent.ClickMainAction -> handleStepTransition()

            DeliveryIntent.ClickCallCustomer -> {
                val phone = currentState.order?.customerPhone ?: ""
                setEffect { DeliveryEffect.OpenDialer(phone) }
            }

            DeliveryIntent.ClickMapNavigation -> {
                setEffect { DeliveryEffect.ShowToast("Đang mở Google Maps...") }
            }

            DeliveryIntent.ClickChatCustomer -> {
                setEffect { DeliveryEffect.ShowToast("Chat với khách hàng") }
            }
        }
    }

    private fun loadOrderData(orderId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(1000) // Mock API Call

            val mockOrder = DeliveryOrderInfo(
                id = orderId,
                restaurantName = "Phở Lý Quốc Sư",
                restaurantAddress = "10 Võ Văn Tần, Q3",
                customerName = "Trần Văn A",
                customerPhone = "0909123456",
                customerAddress = "Landmark 81, Bình Thạnh",
                totalAmount = 155000.0, // Tiền cần thu
                note = "Giao lên sảnh giúp mình nhé"
            )

            setState {
                copy(
                    isLoading = false,
                    order = mockOrder,
                    currentStep = DeliveryStep.HEADING_TO_RESTAURANT // Bắt đầu: Đi đến quán
                )
            }
            startMapSimulation() // Xe bắt đầu chạy
        }
    }

    private fun handleStepTransition() {
        val current = currentState.currentStep

        // Logic chuyển bước
        val nextStep = when(current) {
            DeliveryStep.HEADING_TO_RESTAURANT -> DeliveryStep.PICKING_UP // Đến quán -> Lấy món
            DeliveryStep.PICKING_UP -> DeliveryStep.DELIVERING           // Lấy xong -> Đi giao
            DeliveryStep.DELIVERING -> DeliveryStep.ARRIVED              // Đến nơi -> Hoàn tất
            DeliveryStep.ARRIVED -> null // Kết thúc
        }

        if (nextStep != null) {
            setState { copy(currentStep = nextStep, mapProgress = 0f) }

            // Nếu bước tiếp theo cần di chuyển (Đi giao), chạy lại simulation
            if (nextStep == DeliveryStep.DELIVERING) {
                startMapSimulation()
            }
        } else {
            // Đã hoàn tất toàn bộ -> Quay về Dashboard
            setEffect { DeliveryEffect.ShowToast("Hoàn thành đơn hàng! Nhận 35k") }
            setEffect { DeliveryEffect.NavigateBackDashboard }
        }
    }

    // Giả lập xe chạy trên bản đồ (0 -> 1)
    private fun startMapSimulation() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            var p = 0f
            while (p <= 1f) {
                setState { copy(mapProgress = p) }
                p += 0.01f // Tốc độ chạy
                delay(50)
            }
        }
    }
}