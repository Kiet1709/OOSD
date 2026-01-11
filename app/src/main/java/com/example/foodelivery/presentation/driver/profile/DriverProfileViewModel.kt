package com.example.foodelivery.presentation.driver.profile

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.driver.profile.contract.DriverProfileEffect
import com.example.foodelivery.presentation.driver.profile.contract.DriverProfileIntent
import com.example.foodelivery.presentation.driver.profile.contract.DriverProfileState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : BaseViewModel<DriverProfileState, DriverProfileIntent, DriverProfileEffect>(DriverProfileState()) {

    init {
        setEvent(DriverProfileIntent.LoadProfile)
    }

    fun setEvent(intent: DriverProfileIntent) = handleIntent(intent)

    override fun handleIntent(intent: DriverProfileIntent) {
        when (intent) {
            DriverProfileIntent.LoadProfile -> loadProfile()
            DriverProfileIntent.EditProfile -> setEffect { DriverProfileEffect.NavigateToEditProfile }
            DriverProfileIntent.ClickBack -> setEffect { DriverProfileEffect.NavigateBack }
            DriverProfileIntent.ClickLogout -> {
                viewModelScope.launch {
                    userRepository.logout()
                    setEffect { DriverProfileEffect.NavigateToLogin }
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            setState { copy(isLoading = true) }
            val result = userRepository.getUser(uid)
            if (result is Resource.Success) {
                setState { copy(isLoading = false, user = result.data) }
            } else {
                setState { copy(isLoading = false) }
            }
        }
    }
}