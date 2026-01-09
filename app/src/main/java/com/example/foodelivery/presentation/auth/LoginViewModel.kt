package com.example.foodelivery.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.usecase.auth.CheckUserRoleUseCase
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
    private val loginUseCase: LoginUseCase,
    private val checkUserRoleUseCase: CheckUserRoleUseCase
) : ViewModel() {

    // 1. STATE MANAGEMENT
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    // 2. SIDE EFFECTS (Navigation, Toast)
    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    // 3. INTENT PROCESSING (Nhận hành động từ UI)
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

    // 4. LOGIC NGHIỆP VỤ
    private fun login() {
        val email = state.value.email
        val pass = state.value.pass

        // Validate cơ bản
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

            // Gọi UseCase Login
            val result = loginUseCase(email, pass)


            _state.update { it.copy(isLoading = false) }

            when (result) {
                is Resource.Success -> {
                    // Login thành công -> Check Role để điều hướng
                    val role = checkUserRoleUseCase.invoke() // Lấy role từ UseCase
                    android.util.Log.e("DEBUG_LOGIN", "=== LOGIN SUCCESS ===")
                    android.util.Log.e("DEBUG_LOGIN", "Role lấy về: '$role'")
                    android.util.Log.e("DEBUG_LOGIN", "Role lowercase: '${role.lowercase()}'")
                    android.util.Log.e("DEBUG_LOGIN", "So sánh với 'admin': ${role.lowercase() == "admin"}")
                    navigateByRole(role)
                }
                
                is Resource.Error -> {
                    val errorMsg = result.message ?: "Đăng nhập thất bại"
                    sendEffect(LoginEffect.ShowToast(errorMsg))
                }
                is Resource.Loading -> { /* Đã handle loading state */ }
            }
        }
    }

    private fun navigateByRole(role: String) {
        android.util.Log.e("DEBUG_LOGIN", "Đang xử lý navigation với role: '$role' (lowercase: '${role.lowercase()}')")

        when (role.lowercase()) {
            "admin" -> sendEffect(LoginEffect.Navigation.ToAdminDashboard)
            "driver" -> sendEffect(LoginEffect.Navigation.ToDriverDashboard)
            else -> sendEffect(LoginEffect.Navigation.ToCustomerHome) // CUSTOMER
        }
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}