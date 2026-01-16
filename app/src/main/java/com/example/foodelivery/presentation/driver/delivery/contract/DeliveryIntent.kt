package com.example.foodelivery.presentation.driver.delivery.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class DeliveryIntent : ViewIntent {
    data class LoadOrder(val orderId: String) : DeliveryIntent()
    object ClickMainAction : DeliveryIntent() // Nút to nhất (Đã đến/Đã lấy...)
    object ClickCallCustomer : DeliveryIntent()
    object ClickChatCustomer : DeliveryIntent()
    object ClickMapNavigation : DeliveryIntent() // Mở Google Maps thật
    object MarkAsDelivered : DeliveryIntent()

}