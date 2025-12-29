package com.example.foodelivery.presentation.customer.tracking.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class TrackingIntent : ViewIntent {
    data class LoadTracking(val orderId: String) : TrackingIntent()
    object ClickCallDriver : TrackingIntent()
    object ClickMessageDriver : TrackingIntent()
    object ClickBack : TrackingIntent()
}