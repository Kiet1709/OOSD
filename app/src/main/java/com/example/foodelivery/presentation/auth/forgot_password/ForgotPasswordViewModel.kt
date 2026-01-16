package com.example.foodelivery.presentation.auth.forgot_password

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.usecase.auth.ForgotPasswordUseCase
import com.example.foodelivery.presentation.auth.forgot_password.contract.ForgotPasswordEffect
import com.example.foodelivery.presentation.auth.forgot_password.contract.ForgotPasswordIntent
import com.example.foodelivery.presentation.auth.forgot_password.contract.ForgotPasswordState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    // 1. STATE
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    // 2. EFFECT
    private val _effect = Channel<ForgotPasswordEffect>()
    val effect = _effect.receiveAsFlow()

    // 3. PROCESS INTENT
    fun processIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            // --- STEP 1 ---
            is ForgotPasswordIntent.EmailChanged -> {
                updateState { copy(email = intent.value, error = null) }
            }
            ForgotPasswordIntent.ClickSendOtp -> sendOtp()

            // --- STEP 2 ---
            is ForgotPasswordIntent.OtpChanged -> {
                updateState { copy(otpCode = intent.value, error = null) }
            }
            is ForgotPasswordIntent.NewPassChanged -> {
                updateState { copy(newPass = intent.value, error = null) }
            }
            ForgotPasswordIntent.TogglePasswordVisibility -> {
                updateState { copy(isPasswordVisible = !isPasswordVisible) }
            }
            ForgotPasswordIntent.ClickSubmitReset -> submitReset()

            // --- NAVIGATION ---
            ForgotPasswordIntent.ClickBackToLogin -> {
                sendEffect(ForgotPasswordEffect.Navigation.ToLogin)
            }        }
    }

    // XỬ LÝ STEP 1: Gửi Email/OTP
    private fun sendOtp() {
        val email = _state.value.email

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            updateState { copy(error = "Email không hợp lệ") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            // Gọi UseCase gửi Email (đã tạo ở bước trước)
            val result = forgotPasswordUseCase(email)

            updateState { copy(isLoading = false) }

            when (result) {
                is Resource.Success -> {
                    sendEffect(ForgotPasswordEffect.ShowToast("Mã xác thực đã được gửi!"))
                    // Chuyển sang Step 2
                    updateState { copy(step = 2, error = null) }
                }
                is Resource.Error -> {
                    updateState { copy(error = result.message) }
                    sendEffect(ForgotPasswordEffect.ShowToast(result.message ?: "Lỗi gửi yêu cầu"))
                }
                is Resource.Loading -> {}
            }
        }
    }

    // XỬ LÝ STEP 2: Đổi mật khẩu
    private fun submitReset() {
        val s = _state.value

        // Validate OTP
        if (s.otpCode.length < 4) { // Giả sử OTP 4 hoặc 6 số
            updateState { copy(error = "Mã OTP không hợp lệ") }
            return
        }

        // Validate New Password
        if (s.newPass.length < 6) {
            updateState { copy(error = "Mật khẩu mới phải từ 6 ký tự trở lên") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            kotlinx.coroutines.delay(1500) // Fake delay

            updateState { copy(isLoading = false) }

            // Giả sử thành công
            sendEffect(ForgotPasswordEffect.ShowToast("Đổi mật khẩu thành công!"))
            sendEffect(ForgotPasswordEffect.Navigation.ToLogin)        }
    }

    private fun updateState(reducer: ForgotPasswordState.() -> ForgotPasswordState) {
        _state.update(reducer)
    }

    private fun sendEffect(effect: ForgotPasswordEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}