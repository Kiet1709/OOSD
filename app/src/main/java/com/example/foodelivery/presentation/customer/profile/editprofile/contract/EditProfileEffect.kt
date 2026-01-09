package com.example.foodelivery.presentation.customer.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class EditProfileEffect : ViewSideEffect {
    object GoBack : EditProfileEffect()
    data class ShowToast(val msg: String) : EditProfileEffect()
}