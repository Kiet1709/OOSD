package com.example.foodelivery.presentation.driver.profile.editprofile

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.driver.profile.editprofile.contract.DriverEditProfileEffect
import com.example.foodelivery.presentation.driver.profile.editprofile.contract.DriverEditProfileIntent
import com.example.foodelivery.presentation.driver.profile.editprofile.contract.DriverEditProfileState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverEditProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : BaseViewModel<DriverEditProfileState, DriverEditProfileIntent, DriverEditProfileEffect>(DriverEditProfileState()) {

    init {
        setEvent(DriverEditProfileIntent.LoadData)
    }

    fun setEvent(intent: DriverEditProfileIntent) = handleIntent(intent)

    override fun handleIntent(intent: DriverEditProfileIntent) {
        when (intent) {
            is DriverEditProfileIntent.LoadData -> loadProfile()
            is DriverEditProfileIntent.ChangeName -> setState { copy(name = intent.value) }
            is DriverEditProfileIntent.ChangePhone -> setState { copy(phone = intent.value) }
            is DriverEditProfileIntent.ChangeAvatar -> setState { copy(avatarUrl = intent.value) }
            is DriverEditProfileIntent.Save -> saveProfile()
            is DriverEditProfileIntent.ClickBack -> setEffect { DriverEditProfileEffect.NavigateBack }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            setState { copy(isLoading = true) }
            val result = userRepository.getUser(uid)
            if (result is Resource.Success) {
                val user = result.data
                setState {
                    copy(
                        isLoading = false,
                        name = user?.name ?: "",
                        phone = user?.phoneNumber ?: "",
                        avatarUrl = user?.avatarUrl ?: ""
                    )
                }
            } else {
                setState { copy(isLoading = false) }
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            setState { copy(isLoading = true) }
            val result = userRepository.updateProfile(
                uid = uid,
                name = currentState.name,
                phone = currentState.phone,
                address = "", // Not used for drivers
                avatarUrl = currentState.avatarUrl,
                coverPhotoUrl = null
            )
            if (result is Resource.Success) {
                setEffect { DriverEditProfileEffect.NavigateBack }
            } else {
                setEffect { DriverEditProfileEffect.ShowToast(result.message ?: "Lỗi cập nhật") }
            }
            setState { copy(isLoading = false) }
        }
    }
}