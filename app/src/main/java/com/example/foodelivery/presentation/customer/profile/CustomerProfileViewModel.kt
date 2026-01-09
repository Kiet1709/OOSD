package com.example.foodelivery.presentation.customer.profile

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.customer.profile.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository // Dùng Repository trực tiếp
) : BaseViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState()) {

    init {
        handleIntent(ProfileIntent.LoadProfile)
    }

    fun setEvent(intent: ProfileIntent) = handleIntent(intent)

    override fun handleIntent(intent: ProfileIntent) {
        when(intent) {
            ProfileIntent.LoadProfile -> loadUserProfile()

            // Navigation Events
            ProfileIntent.ClickBack -> setEffect { ProfileEffect.NavigateBack }
            ProfileIntent.ClickEditProfile -> setEffect { ProfileEffect.NavigateToEditProfile }

            // Logout
            ProfileIntent.ClickLogout -> logout()

            // Các tính năng đang phát triển -> Toast
            ProfileIntent.ClickAddress,
            ProfileIntent.ClickOrderHistory,
            ProfileIntent.ClickPaymentMethods,
            ProfileIntent.ClickSupport -> setEffect { ProfileEffect.ShowToast("Tính năng đang phát triển") }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Lấy User và gán thẳng vào State
            userRepository.getUser().collectLatest { user ->
                setState { copy(isLoading = false, user = user) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            setEffect { ProfileEffect.ShowToast("Đã đăng xuất") }
            setEffect { ProfileEffect.NavigateToLogin }
        }
    }
}