package com.example.foodelivery.domain.usecase.order

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IOrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(
    private val repository: IOrderRepository
) {
    suspend operator fun invoke(orderId: String, status: String): Resource<Boolean> {
        // Admin update thì không cần driverId
        return repository.updateOrderStatus(orderId, status, null)
    }
}