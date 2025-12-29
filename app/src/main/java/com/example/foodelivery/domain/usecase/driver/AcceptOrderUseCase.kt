package com.example.foodelivery.domain.usecase.driver

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.domain.repository.IOrderRepository
import javax.inject.Inject

class AcceptOrderUseCase @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(orderId: String): Resource<Boolean> {
        val driver = authRepository.getCurrentUser()
            ?: return Resource.Error("Lỗi xác thực tài xế")

        // Update trạng thái -> CONFIRMED và gán DriverID vào đơn
        return orderRepository.updateOrderStatus(
            orderId = orderId,
            status = OrderStatus.CONFIRMED.value,
            driverId = driver.id
        )
    }
}