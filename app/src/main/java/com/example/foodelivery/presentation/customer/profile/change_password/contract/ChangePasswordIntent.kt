package com.example.foodelivery.presentation.customer.profile.change_password.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class ChangePasswordIntent : ViewIntent {
    data class CurrentPassChange(val value: String) : ChangePasswordIntent()
    data class NewPassChange(val value: String) : ChangePasswordIntent()
    data class ConfirmPassChange(val value: String) : ChangePasswordIntent()
    object ClickSubmit : ChangePasswordIntent()
    object ClickBack : ChangePasswordIntent()
}