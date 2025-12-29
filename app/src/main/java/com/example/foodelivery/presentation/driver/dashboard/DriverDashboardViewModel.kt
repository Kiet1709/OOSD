package com.example.foodelivery.presentation.driver.dashboard

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.presentation.driver.dashboard.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverDashboardViewModel @Inject constructor(
    // Inject UseCases: GetAvailableOrdersUseCase, ToggleDriverStatusUseCase...
) : BaseViewModel<DriverDashboardState, DriverDashboardIntent, DriverDashboardEffect>(DriverDashboardState()) {

    init {
        handleIntent(DriverDashboardIntent.LoadDashboard)
    }

    // Public fun cho UI gọi
    fun setEvent(intent: DriverDashboardIntent) = handleIntent(intent)

    override fun handleIntent(intent: DriverDashboardIntent) {
        when(intent) {
            DriverDashboardIntent.LoadDashboard -> loadData()

            DriverDashboardIntent.ToggleOnlineStatus -> {
                val newState = !currentState.isOnline
                setState { copy(isOnline = newState) }
                if (newState) {
                    loadAvailableOrders() // Online thì mới load đơn
                    setEffect { DriverDashboardEffect.ShowToast("Bạn đang trực tuyến!") }
                } else {
                    setState { copy(availableOrders = emptyList()) }
                    setEffect { DriverDashboardEffect.ShowToast("Bạn đã offline") }
                }
            }

            is DriverDashboardIntent.AcceptOrder -> {
                // Logic nhận đơn: Gọi API -> Success -> Navigate
                viewModelScope.launch {
                    setEffect { DriverDashboardEffect.ShowToast("Đã nhận đơn! Đang điều hướng...") }
                    delay(500)
                    setEffect { DriverDashboardEffect.NavigateToDelivery(intent.orderId) }
                }
            }

            is DriverDashboardIntent.RejectOrder -> {
                // Loại bỏ đơn khỏi list local
                val newList = currentState.availableOrders.filter { it.id != intent.orderId }
                setState { copy(availableOrders = newList) }
            }

            DriverDashboardIntent.ClickRevenueDetail -> setEffect { DriverDashboardEffect.NavigateToRevenueReport }
        }
    }

    private fun loadData() {
        // Giả lập load doanh thu
        setState { copy(todayRevenue = 450000.0) }
    }

    private fun loadAvailableOrders() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            delay(1000) // Mock API call

            val mockOrders = listOf(
                DriverOrderUiModel(
                    "O1", "Cơm Tấm Cali", "123 Nguyễn Trãi, Q1",
                    "45 Lê Lợi, Q1", 2.5, 35000.0, "Vừa xong"
                ),
                DriverOrderUiModel(
                    "O2", "Trà Sữa Koi Thé", "Center Point, Q3",
                    "Landmark 81, Bình Thạnh", 5.8, 55000.0, "5 phút trước"
                )
            )

            setState { copy(isLoading = false, availableOrders = mockOrders) }
        }
    }
}