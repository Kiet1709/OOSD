package com.example.foodelivery.presentation.driver.profile.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.User

data class DriverProfileState(
    val isLoading: Boolean = false,
    val user: User? = null
) : ViewState

sealed class DriverProfileIntent : ViewIntent {
    object LoadProfile : DriverProfileIntent()
    object EditProfile : DriverProfileIntent()
    object ClickBack : DriverProfileIntent()
    object ClickLogout : DriverProfileIntent()
}

sealed class DriverProfileEffect : ViewSideEffect {
    object NavigateToEditProfile : DriverProfileEffect()
    object NavigateBack : DriverProfileEffect()
    object NavigateToLogin : DriverProfileEffect()
    data class ShowToast(val message: String) : DriverProfileEffect()
}