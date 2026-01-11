package com.example.foodelivery.presentation.driver.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState

data class DriverEditProfileState(
    val isLoading: Boolean = false,
    val name: String = "",
    val phone: String = "",
    val avatarUrl: String = ""
) : ViewState

sealed class DriverEditProfileIntent : ViewIntent {
    object LoadData : DriverEditProfileIntent()
    data class ChangeName(val value: String) : DriverEditProfileIntent()
    data class ChangePhone(val value: String) : DriverEditProfileIntent()
    data class ChangeAvatar(val value: String) : DriverEditProfileIntent()
    object Save : DriverEditProfileIntent()
    object ClickBack : DriverEditProfileIntent()
}

sealed class DriverEditProfileEffect : ViewSideEffect {
    object NavigateBack : DriverEditProfileEffect()
    data class ShowToast(val message: String) : DriverEditProfileEffect()
}