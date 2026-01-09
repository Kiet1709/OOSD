package com.example.foodelivery.presentation.driver.profile.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class DriverProfileIntent : ViewIntent {
    object LoadProfile : DriverProfileIntent()
    object ClickBack : DriverProfileIntent()
    object ClickEditProfile : DriverProfileIntent()
    object ClickLogout : DriverProfileIntent()

}