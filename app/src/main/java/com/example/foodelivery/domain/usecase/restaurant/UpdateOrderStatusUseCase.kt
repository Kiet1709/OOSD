package com.example.foodelivery.domain.usecase.restaurant

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(
    private val orderRepository: IOrderRepository
) {
    suspend operator fun invoke(orderId: String, newStatus: OrderStatus): Resource<Boolean> {
        return orderRepository.updateOrderStatus(orderId, newStatus.name, null)
    }
}