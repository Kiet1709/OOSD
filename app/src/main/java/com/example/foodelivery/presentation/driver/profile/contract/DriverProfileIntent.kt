package com.example.foodelivery.presentation.driver.profile.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class DriverProfileIntent : ViewIntent {
    object LoadProfile : DriverProfileIntent()
    object EditProfile : DriverProfileIntent()
    object ClickBack : DriverProfileIntent()
    object ClickLogout : DriverProfileIntent()
    object ClickChangePassword : DriverProfileIntent()
}