package com.example.foodelivery.presentation.driver.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class DriverEditProfileIntent : ViewIntent {
    object LoadData : DriverEditProfileIntent()
    data class ChangeName(val value: String) : DriverEditProfileIntent()
    data class ChangePhone(val value: String) : DriverEditProfileIntent()
    data class ChangeAvatar(val value: String) : DriverEditProfileIntent()
    object Save : DriverEditProfileIntent()
    object ClickBack : DriverEditProfileIntent()
}