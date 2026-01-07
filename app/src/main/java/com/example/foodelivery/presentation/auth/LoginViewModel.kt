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

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> _state.update { it.copy(email = intent.email, emailError = null) }
            is LoginIntent.PasswordChanged -> _state.update { it.copy(pass = intent.pass, passwordError = null) }
            LoginIntent.TogglePasswordVisibility -> _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            LoginIntent.SubmitLogin -> login()
            
            LoginIntent.ClickRegister -> {
                val role = state.value.selectedRole
                // [FIX]: Gửi role kèm theo qua route
                sendEffect(LoginEffect.Navigation.ToRegister(preSelectedRole = role))
            }
            LoginIntent.ClickForgotPassword -> sendEffect(LoginEffect.Navigation.ToForgotPassword)
            
            is LoginIntent.SwitchLoginMode -> switchRole(intent.role)
        }
    }
    
    fun switchRole(role: String) {
        _state.update { it.copy(selectedRole = role) }
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
                    val dbRole = user?.role ?: "CUSTOMER"
                    val currentMode = state.value.selectedRole

                    if (validateRole(dbRole, currentMode)) {
                        sendEffect(LoginEffect.ShowToast("Xin chào ${user?.name}, đăng nhập thành công!"))
                        navigateByRole(dbRole)
                    } else {
                        sendEffect(LoginEffect.ShowToast("Lỗi: Tài khoản là $dbRole, không thể đăng nhập ở chế độ $currentMode"))
                    }
                }
                is Resource.Error -> {
                    val errorMsg = result.message ?: "Đăng nhập thất bại"
                    sendEffect(LoginEffect.ShowToast(errorMsg))
                }
                else -> {}
            }
        }
    }

    private fun validateRole(dbRole: String, currentMode: String): Boolean {
        val db = dbRole.uppercase()
        val mode = currentMode.uppercase()

        if (db == "ADMIN") return true
        if (mode == "CUSTOMER" && db == "CUSTOMER") return true
        if (mode == "DRIVER" && (db == "DRIVER" || db == "SHIPPER")) return true
        if (mode == "STORE" && db == "STORE") return true

        return false
    }

    private fun navigateByRole(role: String) {
        when (role.uppercase()) {
            "ADMIN", "STORE" -> sendEffect(LoginEffect.Navigation.ToAdminDashboard)
            "DRIVER", "SHIPPER" -> sendEffect(LoginEffect.Navigation.ToDriverDashboard)
            else -> sendEffect(LoginEffect.Navigation.ToCustomerHome)
        }
    }

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}