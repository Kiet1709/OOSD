package com.example.foodelivery.presentation.customer.tracking.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class TrackingEffect : ViewSideEffect {
    data class OpenDialer(val phone: String) : TrackingEffect()
    object NavigateBack : TrackingEffect()
    data class ShowToast(val message: String) : TrackingEffect()
}