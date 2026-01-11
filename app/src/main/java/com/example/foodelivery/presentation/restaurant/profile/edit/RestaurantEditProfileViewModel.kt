package com.example.foodelivery.presentation.restaurant.profile.edit

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IUserRepository
import com.example.foodelivery.presentation.restaurant.profile.edit.contract.RestaurantEditProfileEffect
import com.example.foodelivery.presentation.restaurant.profile.edit.contract.RestaurantEditProfileIntent
import com.example.foodelivery.presentation.restaurant.profile.edit.contract.RestaurantEditProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantEditProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : BaseViewModel<RestaurantEditProfileState, RestaurantEditProfileIntent, RestaurantEditProfileEffect>(RestaurantEditProfileState()) {

    init {
        handleIntent(RestaurantEditProfileIntent.LoadData)
    }

    fun setEvent(intent: RestaurantEditProfileIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: RestaurantEditProfileIntent) {
        when (intent) {
            is RestaurantEditProfileIntent.LoadData -> loadData()
            is RestaurantEditProfileIntent.OnNameChange -> setState { copy(name = intent.value) }
            is RestaurantEditProfileIntent.OnAddressChange -> setState { copy(address = intent.value) }
            is RestaurantEditProfileIntent.OnPhoneNumberChange -> setState { copy(phoneNumber = intent.value) }
            is RestaurantEditProfileIntent.OnAvatarUrlChange -> setState { copy(avatarUrl = intent.value) }
            is RestaurantEditProfileIntent.OnCoverPhotoUrlChange -> setState { copy(coverPhotoUrl = intent.value) }
            is RestaurantEditProfileIntent.SaveChanges -> saveChanges()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val user = userRepository.getUser().first()
            if (user != null) {
                setState {
                    copy(
                        isLoading = false,
                        user = user,
                        name = user.name,
                        address = user.address ?: "",
                        phoneNumber = user.phoneNumber,
                        avatarUrl = user.avatarUrl,
                        coverPhotoUrl = user.coverPhotoUrl
                    )
                }
            } else {
                setState { copy(isLoading = false) }
                setEffect { RestaurantEditProfileEffect.ShowToast("Failed to load user data") }
            }
        }
    }

    private fun saveChanges() {
        val currentState = uiState.value
        val currentUser = currentState.user ?: return

        val updatedUser = currentUser.copy(
            name = currentState.name,
            address = currentState.address,
            phoneNumber = currentState.phoneNumber,
            avatarUrl = currentState.avatarUrl,
            coverPhotoUrl = currentState.coverPhotoUrl
        )

        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val result = userRepository.updateUser(updatedUser)
            setState { copy(isLoading = false) }

            if (result.isSuccess) {
                setEffect { RestaurantEditProfileEffect.ShowToast("Profile updated successfully") }
                setEffect { RestaurantEditProfileEffect.NavigateBack }
            } else {
                setEffect { RestaurantEditProfileEffect.ShowToast("Failed to update profile") }
            }
        }
    }
}