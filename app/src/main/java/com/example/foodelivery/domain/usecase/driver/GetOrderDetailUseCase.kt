package com.example.foodelivery.domain.usecase.driver

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.repository.IOrderRepository
import javax.inject.Inject

class GetOrderDetailUseCase @Inject constructor(
    private val orderRepository: IOrderRepository
) {
    suspend operator fun invoke(orderId: String): Resource<Order> {
        if (orderId.isBlank()) {
            return Resource.Error("Order ID không hợp lệ")
        }

        return try {
            val order = orderRepository.getOrderById(orderId)

            if (order == null) {
                return Resource.Error("Không tìm thấy đơn hàng")
            }

            Resource.Success(order)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi tải chi tiết đơn hàng")
        }
    }
}