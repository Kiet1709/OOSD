package com.example.foodelivery.presentation.driver.profile.change_password.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class ChangePasswordEffect : ViewSideEffect {
    data class ShowToast(val message: String) : ChangePasswordEffect()
    object NavigateBack : ChangePasswordEffect()
}