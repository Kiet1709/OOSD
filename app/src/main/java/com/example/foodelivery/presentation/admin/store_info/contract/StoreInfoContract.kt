package com.example.foodelivery.presentation.admin.store_info.contract

import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.StoreInfo

data class StoreInfoState(
    val isLoading: Boolean = false,
    val info: StoreInfo? = null,
    
    // Form data
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val description: String = "",
    val avatarUrl: String = "",
    val coverUrl: String = ""
) : ViewState

sealed class StoreInfoIntent : ViewIntent {
    object LoadData : StoreInfoIntent()
    data class UpdateName(val value: String) : StoreInfoIntent()
    data class UpdateAddress(val value: String) : StoreInfoIntent()
    data class UpdatePhone(val value: String) : StoreInfoIntent()
    data class UpdateDescription(val value: String) : StoreInfoIntent()
    data class UpdateAvatar(val value: String) : StoreInfoIntent()
    data class UpdateCover(val value: String) : StoreInfoIntent()
    object Save : StoreInfoIntent()
}

sealed class StoreInfoEffect : ViewSideEffect {
    data class ShowToast(val message: String) : StoreInfoEffect()
    object NavigateBack : StoreInfoEffect()
}