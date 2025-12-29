package com.example.foodelivery.domain.usecase.order

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllOrdersUseCase @Inject constructor(
    private val repository: IOrderRepository
) {
    operator fun invoke(): Flow<Resource<List<Order>>> = repository.getAllOrders()
}
