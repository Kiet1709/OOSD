package com.example.foodelivery.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.usecase.auth.LoginUseCase
import com.example.foodelivery.presentation.auth.contract.LoginEffect
import com.example.foodelivery.presentation.auth.contract.LoginIntent
import com.example.foodelivery.presentation.auth.contract.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> {
                _state.update { it.copy(email = intent.email, emailError = null) }
            }
            is LoginIntent.PasswordChanged -> {
                _state.update { it.copy(pass = intent.pass, passwordError = null) }
            }
            LoginIntent.TogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            LoginIntent.SubmitLogin -> login()
            LoginIntent.ClickRegister -> sendEffect(LoginEffect.Navigation.ToRegister)
            LoginIntent.ClickForgotPassword -> sendEffect(LoginEffect.Navigation.ToForgotPassword)
            LoginIntent.ClickSkipLogin -> sendEffect(LoginEffect.Navigation.ToCustomerHome)
        }
    }

    private fun login() {
        val email = state.value.email
        val pass = state.value.pass

        if (email.isBlank()) {
            _state.update { it.copy(emailError = "Email không được để trống") }
            return
        }
        if (pass.isBlank()) {
            _state.update { it.copy(passwordError = "Mật khẩu không được để trống") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = loginUseCase(email, pass)
            _state.update { it.copy(isLoading = false) }

            when (result) {
                is Resource.Success -> {
                    val user = result.data
                    if (user != null) {
                        navigateByUser(user)
                    } else {
                        sendEffect(LoginEffect.ShowToast("Không thể lấy thông tin người dùng"))
                    }
                }
                is Resource.Error -> {
                    val errorMsg = result.message ?: "Đăng nhập thất bại"
                    sendEffect(LoginEffect.ShowToast(errorMsg))
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun navigateByUser(user: User) {
        when {
            user.isAdmin() -> sendEffect(LoginEffect.Navigation.ToAdminDashboard)
            user.isRestaurant() -> sendEffect(LoginEffect.Navigation.ToRestaurantDashboard) // New
            user.isDriver() -> sendEffect(LoginEffect.Navigation.ToDriverDashboard)
            else -> sendEffect(LoginEffect.Navigation.ToCustomerHome)
        }
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}