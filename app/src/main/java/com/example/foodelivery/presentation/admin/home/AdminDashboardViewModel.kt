package com.example.foodelivery.presentation.admin.home

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.presentation.admin.home.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val authRepo: IAuthRepository,
) : BaseViewModel<AdminDashboardState, AdminDashboardIntent, AdminDashboardEffect>(
    initialState = AdminDashboardState()
) {

    init {
        handleIntent(AdminDashboardIntent.LoadData)
    }

    fun setEvent(intent: AdminDashboardIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: AdminDashboardIntent) {
        when(intent) {
            is AdminDashboardIntent.LoadData -> loadDashboardData()
            is AdminDashboardIntent.ClickLogout -> logout()
            is AdminDashboardIntent.ClickProfile -> setEffect { AdminDashboardEffect.NavigateToProfile }
            is AdminDashboardIntent.ClickSettings -> setEffect { AdminDashboardEffect.ShowToast("Cài đặt: Đang phát triển") }

            // New Menu Navigation
            is AdminDashboardIntent.ClickManageUsers -> setEffect { AdminDashboardEffect.NavigateToManageUsers }
            is AdminDashboardIntent.ClickManageDrivers -> setEffect { AdminDashboardEffect.NavigateToManageDrivers }
            is AdminDashboardIntent.ClickManageRestaurants -> setEffect { AdminDashboardEffect.NavigateToManageRestaurants }
            is AdminDashboardIntent.ClickManageCategory -> setEffect { AdminDashboardEffect.NavigateToCategoryList }
            else -> {}
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val user = authRepo.getCurrentUser()
            setState {
                copy(
                    isLoading = false,
                    adminName = user?.name ?: "Administrator",
                    avatarUrl = "https://ui-avatars.com/api/?name=${user?.name ?: "Admin"}&background=random"
                )
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