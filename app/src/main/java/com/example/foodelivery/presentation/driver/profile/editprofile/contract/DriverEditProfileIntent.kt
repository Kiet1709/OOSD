package com.example.foodelivery.presentation.driver.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class DriverEditProfileIntent : ViewIntent {
    object LoadData : DriverEditProfileIntent()
    data class ChangeName(val value: String) : DriverEditProfileIntent()
    data class ChangePhone(val value: String) : DriverEditProfileIntent()
    data class ChangeAddress(val value: String) : DriverEditProfileIntent()

    object ClickSave : DriverEditProfileIntent()
    object ClickBack : DriverEditProfileIntent()
}