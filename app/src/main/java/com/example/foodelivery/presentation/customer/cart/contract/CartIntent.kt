package com.example.foodelivery.presentation.customer.cart.contract

import com.example.foodelivery.core.base.ViewIntent

sealed class CartIntent : ViewIntent {

    object LoadCart : CartIntent()

    // Đổi itemId -> foodId cho đồng bộ với Repository
    data class IncreaseQty(val foodId: String) : CartIntent()
    data class DecreaseQty(val foodId: String) : CartIntent()
    data class RemoveItem(val foodId: String) : CartIntent()

    object ClickCheckout : CartIntent()
    object ClickGoHome : CartIntent() // Dùng khi giỏ rỗng muốn quay về home mua tiếp
}