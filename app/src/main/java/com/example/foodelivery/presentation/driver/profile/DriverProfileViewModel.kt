package com.example.foodelivery.presentation.driver.profile

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.customer.profile.contract.ProfileEffect
import com.example.foodelivery.presentation.customer.profile.contract.ProfileIntent
import com.example.foodelivery.presentation.driver.profile.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverProfileViewModel @Inject constructor(
    private val authRepository: IAuthRepository, // Để Logout & lấy ID
    private val userRepository: IUserRepository  // Để lấy chi tiết User
) : BaseViewModel<DriverProfileState, DriverProfileIntent, DriverProfileEffect>(DriverProfileState()) {

    init {
        handleIntent(DriverProfileIntent.LoadProfile)
    }
    fun setEvent(intent: DriverProfileIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: DriverProfileIntent) {
        when(intent) {
            DriverProfileIntent.LoadProfile -> loadDriverProfile()

            DriverProfileIntent.ClickBack -> setEffect { DriverProfileEffect.NavigateBack }

            DriverProfileIntent.ClickLogout -> logout()
            DriverProfileIntent.ClickEditProfile -> setEffect { DriverProfileEffect.NavigateToEditProfile }



        }
    }

    private fun loadDriverProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // 1. Lấy User ID hiện tại
            val currentUser = authRepository.getCurrentUser()

            if (currentUser != null) {
                // 2. Lấy thông tin chi tiết từ Firestore
                val userDetail = userRepository.getUserById(currentUser.id)
                setState { copy(isLoading = false, user = userDetail) }
            } else {
                setState { copy(isLoading = false) }
                setEffect { DriverProfileEffect.ShowToast("Không tìm thấy thông tin tài xế") }
                setEffect { DriverProfileEffect.NavigateToLogin }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            authRepository.logout()
            setState { copy(isLoading = false) }
            setEffect { DriverProfileEffect.ShowToast("Đã đăng xuất") }
            setEffect { DriverProfileEffect.NavigateToLogin }
        }
    }
}