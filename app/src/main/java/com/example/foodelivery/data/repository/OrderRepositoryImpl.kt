// ============================================
// Order Repository Implementation
// ============================================

package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.FoodDatabase
import com.example.foodelivery.data.mapper.*
import com.example.foodelivery.data.remote.dto.OrderDto
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.repository.IOrderRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val db: FoodDatabase,
    private val auth: FirebaseAuth
) : IOrderRepository {

    companion object {
        private const val ORDERS_COLLECTION = "orders"
    }

    // ===== 1. PLACE ORDER =====
    override suspend fun placeOrder(order: Order): Resource<String> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId.isNullOrEmpty()) {
                return Resource.Error("Vui lòng đăng nhập")
            }

            if (order.items.isEmpty()) {
                return Resource.Error("Giỏ hàng trống")
            }

            if (order.shippingAddress.isBlank()) {
                return Resource.Error("Vui lòng nhập địa chỉ giao hàng")
            }

            if (order.totalPrice <= 0) {
                return Resource.Error("Giá tiền không hợp lệ")
            }

            val orderRef = firestore.collection(ORDERS_COLLECTION).document()
            val orderId = orderRef.id

            val orderToSave = order.copy(
                id = orderId,
                userId = userId,
                status = OrderStatus.PENDING,
                timestamp = System.currentTimeMillis()
            )

            val orderDto = orderToSave.toDto()
            orderRef.set(orderDto).await()

            db.cartDao().clearCart()

            Resource.Success(orderId)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi đặt hàng")
        }
    }

    // ===== 2. GET ORDER HISTORY =====
    override fun getOrderHistory(userId: String): Flow<Resource<List<Order>>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(Resource.Error("User ID trống"))
            close()
            return@callbackFlow
        }

        try {
            val query = firestore.collection(ORDERS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Lỗi tải lịch sử"))
                    return@addSnapshotListener
                }

                try {
                    val orders = snapshot?.toObjects(OrderDto::class.java)
                        ?.map { it.toDomain() }
                        ?.sortedByDescending { it.timestamp }
                        ?: emptyList()

                    trySend(Resource.Success(orders))
                } catch (e: Exception) {
                    trySend(Resource.Error("Lỗi xử lý dữ liệu"))
                }
            }

            awaitClose {
                listener.remove()
            }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Lỗi tải lịch sử"))
            close()
        }
    }

    // ===== 3. GET ALL ORDERS =====
    override fun getAllOrders(): Flow<Resource<List<Order>>> = callbackFlow {
        try {
            val query = firestore.collection(ORDERS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)

            val listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Lỗi tải danh sách"))
                    return@addSnapshotListener
                }

                try {
                    val orders = snapshot?.toObjects(OrderDto::class.java)
                        ?.map { it.toDomain() }
                        ?.sortedByDescending { it.timestamp }
                        ?: emptyList()

                    trySend(Resource.Success(orders))
                } catch (e: Exception) {
                    trySend(Resource.Error("Lỗi xử lý dữ liệu"))
                }
            }

            awaitClose {
                listener.remove()
            }
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "Lỗi tải danh sách"))
            close()
        }
    }

    // ===== 4. UPDATE ORDER STATUS =====
    override suspend fun updateOrderStatus(
        orderId: String,
        status: String,
        driverId: String?
    ): Resource<Boolean> {
        return try {
            if (orderId.isBlank()) {
                return Resource.Error("Order ID không hợp lệ")
            }

            val updateMap = mutableMapOf<String, Any>("status" to status)

            if (!driverId.isNullOrEmpty()) {
                updateMap["driverId"] = driverId
            }

            firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .update(updateMap)
                .await()

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi cập nhật trạng thái")
        }
    }

    // ===== 5. GET ORDER DETAIL =====
    override suspend fun getOrderDetail(orderId: String): Resource<Order> {
        return try {
            if (orderId.isBlank()) {
                return Resource.Error("Order ID không hợp lệ")
            }

            val snapshot = firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            val orderDto = snapshot.toObject(OrderDto::class.java)

            if (orderDto == null) {
                return Resource.Error("Không tìm thấy đơn hàng")
            }

            val order = orderDto.toDomain()
            Resource.Success(order)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi tải chi tiết đơn hàng")
        }
    }

    // ===== 6. CANCEL ORDER =====
    override suspend fun cancelOrder(orderId: String): Resource<Boolean> {
        return try {
            val orderResult = getOrderDetail(orderId)
            if (orderResult !is Resource.Success) {
                return Resource.Error("Không tìm thấy đơn hàng")
            }

            val order = orderResult.data ?: return Resource.Error("Dữ liệu không hợp lệ")

            if (!order.canBeCancelled) {
                return Resource.Error("Không thể hủy đơn hàng ở trạng thái hiện tại")
            }

            return updateOrderStatus(orderId, OrderStatus.CANCELLED.value, null)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi hủy đơn hàng")
        }
    }

    override suspend fun getOrderById(orderId: String): Order? {
        return try {
            val snapshot = firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            if (snapshot.exists()) {
                val orderDto = snapshot.toObject(OrderDto::class.java)
                orderDto?.toDomain()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}