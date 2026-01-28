package com.example.foodelivery.presentation.customer.profile.change_password

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.presentation.driver.profile.change_password.contract.ChangePasswordEffect
import com.example.foodelivery.presentation.driver.profile.change_password.contract.ChangePasswordIntent
import com.example.foodelivery.presentation.driver.profile.change_password.contract.ChangePasswordState

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepo: IAuthRepository
) : BaseViewModel<ChangePasswordState, ChangePasswordIntent, ChangePasswordEffect>(
    ChangePasswordState()
) {

    fun processIntent(intent: ChangePasswordIntent) = handleIntent(intent)

    override fun handleIntent(intent: ChangePasswordIntent) {
        when(intent) {
            is ChangePasswordIntent.CurrentPassChange -> setState { copy(currentPass = intent.value, error = null) }
            is ChangePasswordIntent.NewPassChange -> setState { copy(newPass = intent.value, error = null) }
            is ChangePasswordIntent.ConfirmPassChange -> setState { copy(confirmPass = intent.value, error = null) }
            ChangePasswordIntent.ClickBack -> setEffect { ChangePasswordEffect.NavigateBack }
            ChangePasswordIntent.ClickSubmit -> submitChange()
        }
    }

    private fun submitChange() {
        val s = uiState.value

        // Validate đầu vào
        if (s.currentPass.isBlank()) {
            setState { copy(error = "Vui lòng nhập mật khẩu hiện tại") }
            return
        }
        if (s.newPass.length < 6) {
            setState { copy(error = "Mật khẩu mới phải từ 6 ký tự trở lên") }
            return
        }
        if (s.newPass != s.confirmPass) {
            setState { copy(error = "Mật khẩu xác nhận không khớp") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val result = authRepo.changePassword(s.currentPass, s.newPass)
            setState { copy(isLoading = false) }

            when(result) {
                is Resource.Success -> {
                    setEffect { ChangePasswordEffect.ShowToast("Đổi mật khẩu thành công!") }
                    setEffect { ChangePasswordEffect.NavigateBack }
                }
                is Resource.Error -> {
                    setState { copy(error = result.message) }
                    setEffect { ChangePasswordEffect.ShowToast(result.message ?: "Lỗi") }
                }
                else -> {}
            }
        }
    }
}