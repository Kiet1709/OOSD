package com.example.foodelivery.presentation.driver.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class DriverEditProfileEffect : ViewSideEffect {
    object GoBack : DriverEditProfileEffect()
    data class ShowToast(val msg: String) : DriverEditProfileEffect()
}