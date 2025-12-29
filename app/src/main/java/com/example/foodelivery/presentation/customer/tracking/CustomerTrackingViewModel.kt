package com.example.foodelivery.presentation.customer.tracking

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.presentation.customer.tracking.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerTrackingViewModel @Inject constructor() :
    BaseViewModel<TrackingState, TrackingIntent, TrackingEffect>(TrackingState()) {

    private var trackingJob: Job? = null

    // [QUAN TRỌNG]: Hàm public để UI gọi
    fun setEvent(intent: TrackingIntent) {
        handleIntent(intent)
    }

    // Init data khi vào màn hình
    fun init(orderId: String) {
        if (trackingJob == null) {
            handleIntent(TrackingIntent.LoadTracking(orderId))
        }
    }

    override fun handleIntent(intent: TrackingIntent) {
        when(intent) {
            is TrackingIntent.LoadTracking -> startTrackingSimulation(intent.orderId)

            TrackingIntent.ClickCallDriver -> {
                val phone = currentState.driver?.phone
                if (phone != null) setEffect { TrackingEffect.OpenDialer(phone) }
            }

            TrackingIntent.ClickMessageDriver -> {
                setEffect { TrackingEffect.ShowToast("Tính năng Chat đang phát triển") }
            }

            TrackingIntent.ClickBack -> setEffect { TrackingEffect.NavigateBack }
        }
    }

    private fun startTrackingSimulation(orderId: String) {
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            setState { copy(isLoading = true) }

            // 1. Giả lập lấy thông tin tài xế từ Server (Delay 1s)
            delay(1000)
            val mockDriver = DriverUiModel(
                id = "DRV-999",
                name = "Trần Văn Shipper",
                licensePlate = "59-X1 999.99",
                phone = "0909123456",
                rating = 4.8,
                avatarUrl = "https://i.pravatar.cc/150?img=11"
            )
            setState { copy(isLoading = false, driver = mockDriver) }

            // 2. Flow giả lập trạng thái đơn hàng (Realtime)
            val trackingFlow = flow {
                // Bước 1: Xác nhận
                emit(TrackingStep.CONFIRMED to 0f)
                delay(3000)

                // Bước 2: Bếp nấu
                emit(TrackingStep.KITCHEN to 0f)
                delay(4000)

                // Bước 3: Tài xế lấy hàng
                emit(TrackingStep.PICKUP to 0.0f)
                delay(2000)
                emit(TrackingStep.PICKUP to 1.0f) // Đã lấy xong
                delay(1000)

                // Bước 4: Đang giao (Xe chạy từ 0% -> 100%)
                var p = 0f
                while (p <= 1f) {
                    emit(TrackingStep.DELIVERING to p)
                    p += 0.05f // Tăng mỗi lần 5%
                    delay(300) // Cập nhật mỗi 300ms
                }

                // Bước 5: Đến nơi
                emit(TrackingStep.ARRIVED to 1f)
            }

            // 3. Collect Flow và update State
            trackingFlow.collect { (step, progress) ->
                val eta = when(step) {
                    TrackingStep.CONFIRMED -> "30 phút"
                    TrackingStep.KITCHEN -> "25 phút"
                    TrackingStep.PICKUP -> "15 phút"
                    TrackingStep.DELIVERING -> "${(15 * (1 - progress)).toInt()} phút" // ETA giảm dần
                    TrackingStep.ARRIVED -> "Đã đến"
                }
                setState { copy(currentStep = step, driverProgress = progress, eta = eta) }
            }
        }
    }
}