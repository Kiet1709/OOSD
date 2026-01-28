package com.example.foodelivery.presentation.auth.reset_password

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.presentation.auth.reset_password.contract.ResetPasswordEffect
import com.example.foodelivery.presentation.auth.reset_password.contract.ResetPasswordIntent
import com.example.foodelivery.presentation.auth.reset_password.contract.ResetPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepo: IAuthRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<ResetPasswordState, ResetPasswordIntent, ResetPasswordEffect>(ResetPasswordState()) {

    private val oobCode: String = savedStateHandle.get<String>("code") ?: ""

    fun processIntent(intent: ResetPasswordIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: ResetPasswordIntent) {
        when (intent) {
            is ResetPasswordIntent.PassChanged -> {
                setState { copy(newPass = intent.value, error = null) }
            }
            ResetPasswordIntent.ClickSubmit -> submitNewPassword()
        }
    }

    private fun submitNewPassword() {
        val pass = uiState.value.newPass
        if (pass.length < 6) {
            setState { copy(error = "Mật khẩu phải từ 6 ký tự trở lên") }
            return
        }
        if (oobCode.isBlank()) {
            setEffect { ResetPasswordEffect.ShowToast("Lỗi: Link không hợp lệ hoặc đã hết hạn") }
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            val result = authRepo.confirmPasswordReset(oobCode, pass)

            setState { copy(isLoading = false) }

            when (result) {
                is Resource.Success -> {
                    setEffect { ResetPasswordEffect.ShowToast("Đổi mật khẩu thành công! Hãy đăng nhập lại.") }
                    setEffect { ResetPasswordEffect.NavigateToLogin }
                }
                is Resource.Error -> {
                    setState { copy(error = result.message) }
                    setEffect { ResetPasswordEffect.ShowToast(result.message ?: "Lỗi hệ thống") }
                }
                else -> {}
            }
        }
    }
}