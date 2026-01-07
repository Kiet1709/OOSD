package com.example.foodelivery.presentation.auth.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.usecase.auth.RegisterUseCase
import com.example.foodelivery.presentation.auth.register.contract.RegisterEffect
import com.example.foodelivery.presentation.auth.register.contract.RegisterIntent
import com.example.foodelivery.presentation.auth.register.contract.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    savedStateHandle: SavedStateHandle // Inject SavedStateHandle
) : BaseViewModel<RegisterState, RegisterIntent, RegisterEffect>(RegisterState()) {

    init {
        // [FIX]: Lấy role từ navigation argument ngay khi khởi tạo ViewModel
        val roleArg = savedStateHandle.get<String>("role")
        if (!roleArg.isNullOrBlank()) {
            setState { copy(role = roleArg) }
        }
    }

    override fun handleIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.NameChanged -> setState { copy(name = intent.name, nameError = null) }
            is RegisterIntent.EmailChanged -> setState { copy(email = intent.email, emailError = null) }
            is RegisterIntent.PhoneChanged -> setState { copy(phone = intent.phone, phoneError = null) }
            is RegisterIntent.PasswordChanged -> setState { copy(pass = intent.password, passError = null) }
            is RegisterIntent.ConfirmPasswordChanged -> setState { copy(confirmPass = intent.confirmPass, confirmPassError = null) }
            
            is RegisterIntent.SelectRole -> setState { copy(role = intent.role) }

            RegisterIntent.TogglePasswordVisibility -> setState { copy(isPasswordVisible = !isPasswordVisible) }
            RegisterIntent.ToggleConfirmPasswordVisibility -> setState { copy(isConfirmPasswordVisible = !isConfirmPasswordVisible) }

            RegisterIntent.SubmitRegister -> register()
            RegisterIntent.ClickLogin -> setEffect { RegisterEffect.Navigation.ToLogin }
        }
    }

    fun processIntent(intent: RegisterIntent) = handleIntent(intent)

    private fun register() {
        val currentState = uiState.value

        if (currentState.name.isBlank()) { setState { copy(nameError = "Thiếu tên") }; return }
        if (currentState.email.isBlank()) { setState { copy(emailError = "Thiếu email") }; return }
        if (currentState.phone.isBlank()) { setState { copy(phoneError = "Thiếu SĐT") }; return }
        if (currentState.pass.length < 6) { setState { copy(passError = "Mật khẩu < 6 ký tự") }; return }
        if (currentState.pass != currentState.confirmPass) { setState { copy(confirmPassError = "Mật khẩu không khớp") }; return }

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            
            val result = registerUseCase(
                name = currentState.name,
                email = currentState.email,
                pass = currentState.pass,
                phone = currentState.phone,
                role = currentState.role // Role này đã được set từ init
            )
            
            when (result) {
                is Resource.Loading<*> -> {
                    setState { copy(isLoading = true) }
                }
                is Resource.Success<*> -> {
                    setState { copy(isLoading = false) }
                    setEffect { RegisterEffect.ShowToast("Đăng ký thành công") }
                    delay(1000)
                    setEffect { RegisterEffect.Navigation.ToLogin }
                }
                is Resource.Error<*> -> {
                    setState { copy(isLoading = false) }
                    setEffect { RegisterEffect.ShowToast(result.message ?: "Đăng ký thất bại") }
                }
            }
        }
    }
}