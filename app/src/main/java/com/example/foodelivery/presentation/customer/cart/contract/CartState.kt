package com.example.foodelivery.presentation.customer.cart.contract

import com.example.foodelivery.core.base.ViewState

data class CartState(
    val isLoading: Boolean = false,
    val items: List<CartItemUiModel> = emptyList(),

    // Thông tin hóa đơn (Bill)
    val subTotal: Double = 0.0,        // Tổng tiền hàng
    val deliveryFee: Double = 15000.0, // Phí ship (Có thể lấy từ config sau này)
    val discountAmount: Double = 0.0,  // Giảm giá
    val address: String = "",
    val finalTotal: Double = 0.0
) : ViewState {
    val isCartEmpty: Boolean get() = items.isEmpty()
}