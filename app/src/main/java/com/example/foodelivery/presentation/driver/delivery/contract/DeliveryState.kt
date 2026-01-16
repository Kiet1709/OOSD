package com.example.foodelivery.presentation.driver.delivery.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.User


// --- STATE ---
data class DeliveryState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val customer: User? = null,
    val restaurant: User? = null,
    val currentStep: DeliveryStep = DeliveryStep.HEADING_TO_RESTAURANT,
    val mapProgress: Float = 0f // 0f -> 1f (Để vẽ xe chạy trên map)
) : ViewState