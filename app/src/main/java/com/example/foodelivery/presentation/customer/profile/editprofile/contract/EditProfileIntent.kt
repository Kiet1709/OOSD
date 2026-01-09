package com.example.foodelivery.presentation.customer.profile.editprofile.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class EditProfileIntent : ViewIntent {
    object LoadData : EditProfileIntent()
    data class ChangeName(val value: String) : EditProfileIntent()
    data class ChangePhone(val value: String) : EditProfileIntent()
    data class ChangeAddress(val value: String) : EditProfileIntent()
    object ClickSave : EditProfileIntent()
    object ClickBack : EditProfileIntent()
}