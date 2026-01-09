package com.example.foodelivery.domain.usecase.order

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllOrdersUseCase @Inject constructor(
    private val orderRepository: IOrderRepository
) {

    /**
     * Lấy tất cả đơn hàng (Real-time Flow)
     * Admin sẽ nhìn thấy updates tức thì từ Firebase
     */
    operator fun invoke(): Flow<Resource<List<Order>>> {
        return orderRepository.getAllOrders()
    }
}