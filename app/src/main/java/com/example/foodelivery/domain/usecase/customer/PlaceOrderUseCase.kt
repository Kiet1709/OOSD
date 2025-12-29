package com.example.foodelivery.domain.usecase.customer

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.domain.repository.IOrderRepository
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        shippingAddress: String,
        items: List<CartItem>,
        totalPrice: Double
    ): Resource<String> {
        // 1. Lấy User hiện tại (Bắt buộc phải có User mới tạo được đơn)
        val currentUser = authRepository.getCurrentUser()
            ?: return Resource.Error("Bạn chưa đăng nhập")

        if (items.isEmpty()) return Resource.Error("Giỏ hàng trống")
        if (shippingAddress.isBlank()) return Resource.Error("Chưa nhập địa chỉ")

        // 2. Tạo Object Order chuẩn Domain
        val newOrder = Order(
            id = "", // Để rỗng, Repository sẽ tự sinh ID hoặc lấy từ Firebase
            userId = currentUser.id,
            driverId = null,
            status = OrderStatus.PENDING,
            totalPrice = totalPrice,
            shippingAddress = shippingAddress,
            timestamp = System.currentTimeMillis(),
            items = items
        )

        // 3. Gọi Repo
        return orderRepository.placeOrder(newOrder)
    }
}