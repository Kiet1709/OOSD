package com.example.foodelivery.presentation.admin.home

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.domain.usecase.admin.GetDashboardStatsUseCase
import com.example.foodelivery.presentation.admin.home.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val authRepo: IAuthRepository,
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase
) : BaseViewModel<AdminDashboardState, AdminDashboardIntent, AdminDashboardEffect>(
    initialState = AdminDashboardState() // <--- FIX LỖI TẠI ĐÂY: Truyền thẳng vào constructor
) {

    init {
        handleIntent(AdminDashboardIntent.LoadData)
    }

    // Hàm public để UI gọi
    fun setEvent(intent: AdminDashboardIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: AdminDashboardIntent) {
        when(intent) {
            is AdminDashboardIntent.LoadData -> loadDashboardData(false)
            is AdminDashboardIntent.RefreshData -> loadDashboardData(true)

            is AdminDashboardIntent.ClickLogout -> logout()
            is AdminDashboardIntent.ClickProfile -> setEffect { AdminDashboardEffect.NavigateToProfile }
            is AdminDashboardIntent.ClickSettings -> setEffect { AdminDashboardEffect.ShowToast("Cài đặt: Đang phát triển") }

            // Navigation
            is AdminDashboardIntent.ClickManageOrders -> setEffect { AdminDashboardEffect.NavigateToManageOrders }
            is AdminDashboardIntent.ClickManageFood -> setEffect { AdminDashboardEffect.NavigateToFoodList }
            is AdminDashboardIntent.ClickManageCategory -> setEffect { AdminDashboardEffect.NavigateToCategoryList }

            // Placeholder
            is AdminDashboardIntent.ClickManageDrivers -> setEffect { AdminDashboardEffect.ShowToast("Tài xế: Coming Soon") }
            is AdminDashboardIntent.ClickManageUsers -> setEffect { AdminDashboardEffect.ShowToast("Người dùng: Coming Soon") }
            is AdminDashboardIntent.ClickPromotions -> setEffect { AdminDashboardEffect.ShowToast("Khuyến mãi: Coming Soon") }
            is AdminDashboardIntent.ClickReviews -> setEffect { AdminDashboardEffect.ShowToast("Đánh giá: Coming Soon") }
            is AdminDashboardIntent.ClickReports -> setEffect { AdminDashboardEffect.ShowToast("Báo cáo: Coming Soon") }
        }
    }

    private fun loadDashboardData(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) setState { copy(isRefreshing = true) } else setState { copy(isLoading = true) }

            // 1. Lấy thông tin Admin
            val user = authRepo.getCurrentUser()
            setState {
                copy(
                    adminName = user?.name ?: "Administrator",
                    avatarUrl = "https://ui-avatars.com/api/?name=${user?.name ?: "Admin"}&background=random"
                )
            }

            // 2. Lấy thống kê Realtime từ UseCase
            getDashboardStatsUseCase().collectLatest { result ->
                when(result) {
                    is Resource.Success -> {
                        val stats = result.data
                        setState {
                            copy(
                                isLoading = false,
                                isRefreshing = false,
                                todayRevenue = stats?.todayRevenue ?: 0.0,
                                totalOrders = stats?.totalOrdersCount ?: 0,
                                pendingOrders = stats?.pendingOrdersCount ?: 0,
                                activeDrivers = 0
                            )
                        }
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false, isRefreshing = false) }
                        setEffect { AdminDashboardEffect.ShowToast(result.message ?: "Lỗi tải dữ liệu") }
                    }
                    is Resource.Loading -> { /* Do nothing */ }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepo.logout()
            setEffect { AdminDashboardEffect.NavigateToLogin }
        }
    }
}