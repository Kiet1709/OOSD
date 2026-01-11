package com.example.foodelivery.presentation.customer.cart.contract

import com.example.foodelivery.core.base.ViewState

// 1. UI Model: Chỉ chứa dữ liệu cần thiết để hiển thị lên màn hình
data class CartItemUiModel(
    val foodId: String, // Khóa chính (trùng với Database)
    val name: String,
    val imageUrl: String,
    val price: Double,
    val quantity: Int,
    val note: String = "",
    val restaurantId: String // Add this field
) {
    val itemTotal: Double get() = price * quantity
}

// 2. State: Chứa toàn bộ trạng thái màn hình
data class CartState(
    val isLoading: Boolean = false,
    val items: List<CartItemUiModel> = emptyList(),

    // Thông tin hóa đơn (Bill)
    val subTotal: Double = 0.0,        // Tổng tiền hàng
    val deliveryFee: Double = 15000.0, // Phí ship (Có thể lấy từ config sau này)
    val discountAmount: Double = 0.0,  // Giảm giá
    val address: String = "",
    // finalTotal sẽ được tính toán tự động trong ViewModel dựa trên 3 biến trên
    val finalTotal: Double = 0.0
) : ViewState {
    val isCartEmpty: Boolean get() = items.isEmpty()
}