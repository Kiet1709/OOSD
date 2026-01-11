package com.example.foodelivery.presentation.restaurant.profile.edit.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User

data class RestaurantEditProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val name: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val avatarUrl: String = "",
    val coverPhotoUrl: String = ""
) : ViewState

sealed class RestaurantEditProfileIntent : ViewIntent {
    object LoadData : RestaurantEditProfileIntent()
    data class OnNameChange(val value: String) : RestaurantEditProfileIntent()
    data class OnAddressChange(val value: String) : RestaurantEditProfileIntent()
    data class OnPhoneNumberChange(val value: String) : RestaurantEditProfileIntent()
    data class OnAvatarUrlChange(val value: String) : RestaurantEditProfileIntent()
    data class OnCoverPhotoUrlChange(val value: String) : RestaurantEditProfileIntent()
    object SaveChanges : RestaurantEditProfileIntent()
}

sealed class RestaurantEditProfileEffect : ViewSideEffect {
    data class ShowToast(val message: String) : RestaurantEditProfileEffect()
    object NavigateBack : RestaurantEditProfileEffect()
}