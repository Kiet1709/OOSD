package com.example.foodelivery.presentation.driver.profile.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class DriverProfileEffect : ViewSideEffect {
    object NavigateToEditProfile : DriverProfileEffect()
    object NavigateBack : DriverProfileEffect()
    object NavigateToLogin : DriverProfileEffect()
    data class ShowToast(val message: String) : DriverProfileEffect()
    object NavigateToChangePassword : DriverProfileEffect()
}