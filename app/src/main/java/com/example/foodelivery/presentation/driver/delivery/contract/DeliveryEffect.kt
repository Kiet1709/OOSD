package com.example.foodelivery.presentation.driver.delivery.contract

import com.example.foodelivery.core.base.ViewSideEffect

sealed class DeliveryEffect : ViewSideEffect {
    object NavigateBackDashboard : DeliveryEffect() // Giao xong về Dashboard
    data class OpenDialer(val phone: String) : DeliveryEffect()
    data class ShowToast(val msg: String) : DeliveryEffect()
}