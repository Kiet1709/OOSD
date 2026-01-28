package com.example.foodelivery.presentation.auth.forgot_password

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IAuthRepository
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
    private val authRepo: IAuthRepository
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
            is ForgotPasswordIntent.EmailChanged -> {
                updateState { copy(email = intent.value, error = null) }
            }
            ForgotPasswordIntent.ClickSendLink -> sendResetLink()
            ForgotPasswordIntent.ClickBackToLogin -> {
                sendEffect(ForgotPasswordEffect.NavigateToLogin)
            }
        }
    }

    private fun sendResetLink() {
        val email = _state.value.email

        // Validate Email
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            updateState { copy(error = "Email không hợp lệ") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }

            // Gọi Repository gửi Link
            val result = authRepo.sendPasswordResetEmail(email)

            updateState { copy(isLoading = false) }

            when (result) {
                is Resource.Success -> {
                    sendEffect(ForgotPasswordEffect.ShowToast("Đã gửi link! Vui lòng kiểm tra Email."))
                    sendEffect(ForgotPasswordEffect.NavigateToLogin)
                }
                is Resource.Error -> {
                    updateState { copy(error = result.message) }
                    sendEffect(ForgotPasswordEffect.ShowToast(result.message ?: "Lỗi gửi yêu cầu"))
                }
                else -> {}
            }
        }
    }

    private fun updateState(reducer: ForgotPasswordState.() -> ForgotPasswordState) {
        _state.update(reducer)
    }

    private fun sendEffect(effect: ForgotPasswordEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}