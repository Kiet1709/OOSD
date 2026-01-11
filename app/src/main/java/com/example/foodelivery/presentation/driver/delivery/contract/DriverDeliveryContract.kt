package com.example.foodelivery.presentation.driver.delivery.contract

import com.example.foodelivery.core.base.ViewSideEffect
import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.User

data class DriverDeliveryState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val customer: User? = null,
    val restaurant: User? = null
) : ViewState

sealed class DriverDeliveryIntent : ViewIntent {
    object MarkAsDelivered : DriverDeliveryIntent()
}

sealed class DriverDeliveryEffect : ViewSideEffect {
    object NavigateBack : DriverDeliveryEffect()
    data class ShowToast(val message: String) : DriverDeliveryEffect()
}
