package com.example.foodelivery.presentation.restaurant.profile.edit.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class RestaurantEditProfileIntent : ViewIntent {
    object LoadData : RestaurantEditProfileIntent()
    data class OnNameChange(val value: String) : RestaurantEditProfileIntent()
    data class OnAddressChange(val value: String) : RestaurantEditProfileIntent()
    data class OnPhoneNumberChange(val value: String) : RestaurantEditProfileIntent()
    data class OnAvatarUrlChange(val value: String) : RestaurantEditProfileIntent()
    data class OnCoverPhotoUrlChange(val value: String) : RestaurantEditProfileIntent()
    object SaveChanges : RestaurantEditProfileIntent()
}