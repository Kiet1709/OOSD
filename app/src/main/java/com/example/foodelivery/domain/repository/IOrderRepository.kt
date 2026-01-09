package com.example.foodelivery.domain.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {
    suspend fun placeOrder(order: Order): Resource<String>
    fun getOrderHistory(userId: String): Flow<Resource<List<Order>>>
    suspend fun updateOrderStatus(orderId: String, status: String, driverId: String?): Resource<Boolean>
    suspend fun getOrderDetail(orderId: String): Resource<Order>
    suspend fun cancelOrder(orderId: String): Resource<Boolean>

    fun getAllOrders(): Flow<Resource<List<Order>>>
    suspend fun getOrderById(orderId: String): Order?
}