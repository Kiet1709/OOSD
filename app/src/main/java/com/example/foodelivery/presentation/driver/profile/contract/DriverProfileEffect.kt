package com.example.foodelivery.presentation.driver.profile.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class DriverProfileEffect : ViewSideEffect {
    data class ShowToast(val msg: String) : DriverProfileEffect()
    object NavigateBack : DriverProfileEffect()
    object NavigateToLogin : DriverProfileEffect()
    object NavigateToEditProfile : DriverProfileEffect()


}