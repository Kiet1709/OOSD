package com.example.foodelivery.domain.usecase.driver

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class DashboardData(
    val pendingOrders: List<Order>,
    val todayRevenue: Double
)

class GetDriverDashboardUseCase @Inject constructor(
    private val orderRepository: IOrderRepository,
    private val authRepository: IAuthRepository
) {
    operator fun invoke(): Flow<Resource<DashboardData>> = flow {
        val user = authRepository.getCurrentUser()
        if (user == null) {
            emit(Resource.Error("Chưa đăng nhập"))
            return@flow
        }

        // Combine: Kết hợp 2 luồng Realtime
        // Flow 1: Tất cả đơn hàng (để lọc đơn PENDING)
        // Flow 2: Lịch sử đơn của tôi (để tính tiền)
        emit(Resource.Loading())

        try {
            combine(
                orderRepository.getAllOrders(),
                orderRepository.getOrderHistory(user.id)
            ) { allOrdersRes, historyRes ->

                // 1. Xử lý logic lọc đơn PENDING và chưa có Driver
                val availableOrders = if (allOrdersRes is Resource.Success) {
                    allOrdersRes.data?.filter {
                        it.status == OrderStatus.PENDING && it.driverId == null
                    } ?: emptyList()
                } else emptyList()

                // 2. Xử lý logic tính doanh thu (đơn COMPLETED)
                val revenue = if (historyRes is Resource.Success) {
                    historyRes.data?.filter {
                        it.status == OrderStatus.COMPLETED
                    }?.sumOf { it.totalPrice } ?: 0.0
                } else 0.0

                // Trả về data đã xử lý
                Resource.Success(DashboardData(availableOrders, revenue))
            }.collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Lỗi không xác định"))
        }
    }
}