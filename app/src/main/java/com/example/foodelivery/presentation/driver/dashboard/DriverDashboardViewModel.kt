package com.example.foodelivery.presentation.driver.dashboard

import android.text.format.DateUtils
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.domain.usecase.driver.AcceptOrderUseCase
import com.example.foodelivery.domain.usecase.driver.GetDriverDashboardUseCase
import com.example.foodelivery.domain.usecase.driver.UpdateLocationUseCase
import com.example.foodelivery.presentation.driver.dashboard.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class DriverDashboardViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val getDashboardUseCase: GetDriverDashboardUseCase,
    private val acceptOrderUseCase: AcceptOrderUseCase,
    private val updateLocationUseCase: UpdateLocationUseCase
) : BaseViewModel<DriverDashboardState, DriverDashboardIntent, DriverDashboardEffect>(DriverDashboardState()) {

    private var realtimeJob: Job? = null

    init {
        // Gọi hàm load user ngay khi khởi tạo
        loadUserProfile()

        handleIntent(DriverDashboardIntent.LoadDashboard)
    }

    fun setEvent(intent: DriverDashboardIntent) = handleIntent(intent)

    override fun handleIntent(intent: DriverDashboardIntent) {
        when(intent) {
            DriverDashboardIntent.LoadDashboard -> { }

            DriverDashboardIntent.Refresh -> {
                if (currentState.isOnline) startRealtimeUpdates()
                else setState { copy(isLoading = false) }
            }

            DriverDashboardIntent.ToggleOnlineStatus -> {
                val newStatus = !currentState.isOnline
                setState { copy(isOnline = newStatus) }

                if (newStatus) {
                    startRealtimeUpdates()
                    updateCurrentLocation()
                    setEffect { DriverDashboardEffect.ShowToast("Bạn đang trực tuyến!") }
                } else {
                    stopRealtimeUpdates()
                    setEffect { DriverDashboardEffect.ShowToast("Bạn đã Offline") }
                }
            }

            is DriverDashboardIntent.AcceptOrder -> acceptOrder(intent.orderId)

            is DriverDashboardIntent.RejectOrder -> {
                val newList = currentState.availableOrders.filter { it.id != intent.orderId }
                setState { copy(availableOrders = newList) }
            }

            DriverDashboardIntent.ClickRevenueDetail -> setEffect { DriverDashboardEffect.NavigateToRevenueReport }
            DriverDashboardIntent.ClickProfile -> setEffect { DriverDashboardEffect.NavigateToProfile }

            DriverDashboardIntent.Logout -> {
                viewModelScope.launch {
                    authRepository.logout()
                    setEffect { DriverDashboardEffect.ShowToast("Đã đăng xuất") }
                    delay(500)
                    setEffect { DriverDashboardEffect.NavigateToLogin }
                }
            }
        }
    }

    // [SỬA LỖI TẠI ĐÂY]: Thêm viewModelScope.launch
    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() // Hàm suspend giờ đã được gọi trong Coroutine
            if (user != null) {
                setState { copy(user = user) }
            }
        }
    }

    private fun startRealtimeUpdates() {
        realtimeJob?.cancel()
        realtimeJob = viewModelScope.launch {
            getDashboardUseCase().collectLatest { result ->
                when(result) {
                    is Resource.Loading -> {
                        if (currentState.availableOrders.isEmpty()) setState { copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val data = result.data!!
                        val uiOrders = data.pendingOrders.map { order ->
                            val rawDistance = Random.nextDouble(1.0, 5.0)
                            val safeDistance = (rawDistance * 10).toInt() / 10.0
                            DriverOrderUiModel(
                                id = order.id,
                                restaurantName = if (order.items.isNotEmpty()) order.items[0].name else "Cửa hàng đối tác",
                                restaurantAddress = "Khu vực trung tâm",
                                customerAddress = order.shippingAddress ?: "",
                                earning = order.totalPrice ?: 0.0,
                                distanceKm = safeDistance,
                                timeAgo = try {
                                    DateUtils.getRelativeTimeSpanString(order.timestamp, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString()
                                } catch (e: Exception) { "Vừa xong" }
                            )
                        }
                        setState {
                            copy(
                                isLoading = false,
                                availableOrders = uiOrders,
                                todayRevenue = data.todayRevenue
                            )
                        }
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect { DriverDashboardEffect.ShowToast(result.message ?: "Lỗi kết nối") }
                    }
                }
            }
        }
    }

    private fun stopRealtimeUpdates() {
        realtimeJob?.cancel()
        setState { copy(availableOrders = emptyList(), isLoading = false) }
    }

    private fun acceptOrder(orderId: String) {
        viewModelScope.launch {
            when(val res = acceptOrderUseCase(orderId)) {
                is Resource.Success -> {
                    setEffect { DriverDashboardEffect.ShowToast("Nhận đơn thành công!") }
                    delay(500)
                    setEffect { DriverDashboardEffect.NavigateToDelivery(orderId) }
                }
                is Resource.Error -> {
                    setEffect { DriverDashboardEffect.ShowToast(res.message ?: "Lỗi nhận đơn") }
                    startRealtimeUpdates()
                }
                else -> {}
            }
        }
    }

    private fun updateCurrentLocation() {
        viewModelScope.launch {
            updateLocationUseCase(10.762622, 106.660172, 0f)
        }
    }
}