package com.example.foodelivery.domain.usecase.order

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IOrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(
    private val orderRepository: IOrderRepository
) {

    suspend operator fun invoke(
        orderId: String,
        status: String,
        driverId: String? = null
    ): Resource<Boolean> {
        if (orderId.isBlank()) {
            return Resource.Error("Order ID không hợp lệ")
        }

        if (status.isBlank()) {
            return Resource.Error("Status không hợp lệ")
        }

        return orderRepository.updateOrderStatus(orderId, status, driverId)
    }
}