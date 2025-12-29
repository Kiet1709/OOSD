package com.example.foodelivery.domain.usecase.admin

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

// Model chứa dữ liệu thống kê
data class DashboardStats(
    val totalRevenue: Double = 0.0,      // Tổng doanh thu
    val todayRevenue: Double = 0.0,      // Doanh thu hôm nay
    val pendingOrdersCount: Int = 0,     // Đơn cần xử lý (Mới)
    val totalOrdersCount: Int = 0        // Tổng số đơn
)

class GetDashboardStatsUseCase @Inject constructor(
    private val orderRepository: IOrderRepository
) {
    operator fun invoke(): Flow<Resource<DashboardStats>> {
        // Lắng nghe stream từ Repository, tự động tính lại khi có đơn mới
        return orderRepository.getAllOrders().map { resource ->
            when (resource) {
                is Resource.Success -> {
                    val orders = resource.data ?: emptyList()
                    val stats = calculateStats(orders)
                    Resource.Success(stats)
                }
                is Resource.Error -> Resource.Error(resource.message ?: "Lỗi tính toán")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }

    private fun calculateStats(orders: List<Order>): DashboardStats {
        val startOfToday = getStartOfDay()

        // 1. Tính doanh thu (Chỉ tính đơn KHÔNG bị hủy)
        val validOrders = orders.filter { it.status != OrderStatus.CANCELLED } // Dùng CANCELLED hoặc HISTORY tùy logic của bạn
        val totalRevenue = validOrders.sumOf { it.totalPrice }

        // 2. Doanh thu hôm nay
        val todayRevenue = validOrders
            .filter { it.timestamp >= startOfToday }
            .sumOf { it.totalPrice }

        // 3. Đơn cần xử lý (Status = NEW hoặc PREPARING)
        val pendingCount = orders.count {
            it.status == OrderStatus.NEW || it.status == OrderStatus.PREPARING
        }

        return DashboardStats(
            totalRevenue = totalRevenue,
            todayRevenue = todayRevenue,
            pendingOrdersCount = pendingCount,
            totalOrdersCount = orders.size
        )
    }

    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}