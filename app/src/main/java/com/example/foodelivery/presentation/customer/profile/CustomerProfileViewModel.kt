package com.example.foodelivery.presentation.customer.profile

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.usecase.profile.GetUserProfileUseCase
import com.example.foodelivery.domain.usecase.profile.LogoutUseCase
import com.example.foodelivery.presentation.customer.profile.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState()) {

    init {
        loadUserProfile()
    }

    fun setEvent(intent: ProfileIntent) = handleIntent(intent)

    override fun handleIntent(intent: ProfileIntent) {
        when(intent) {
            ProfileIntent.LoadProfile -> loadUserProfile()

            // Xử lý Navigation (Điều hướng)
            ProfileIntent.ClickEditProfile -> setEffect { ProfileEffect.NavigateToEditProfile }
            ProfileIntent.ClickAddress -> setEffect { ProfileEffect.NavigateToAddressList }
            ProfileIntent.ClickOrderHistory -> setEffect { ProfileEffect.NavigateToOrderHistory }

            // Các tính năng chưa phát triển -> Show Toast thông báo
            ProfileIntent.ClickPaymentMethods -> setEffect { ProfileEffect.ShowToast("Tính năng Phương thức thanh toán đang phát triển") }
            ProfileIntent.ClickSupport -> setEffect { ProfileEffect.ShowToast("Tính năng Hỗ trợ đang phát triển") }

            ProfileIntent.ClickLogout -> performLogout()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            getUserProfileUseCase().collectLatest { user ->
                setState {
                    copy(
                        isLoading = false,
                        user = user?.toUiModel() // Mapping Domain -> UI
                    )
                }
            }
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            logoutUseCase()
            setEffect { ProfileEffect.NavigateToLogin }
        }
    }
}

// Extension: Chuyển đổi từ Domain User -> UI Model
private fun User.toUiModel(): UserProfileUiModel {
    return UserProfileUiModel(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phoneNumber,
        avatarUrl = this.avatarUrl,
        // Các trường này User.kt không có, ta giả lập hoặc tính toán tại đây
        membershipLevel = "Thành viên Vàng",
        loyaltyPoints = 1250
    )
}