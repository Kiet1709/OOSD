package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.AppDatabase
import com.example.foodelivery.data.mapper.*
import com.example.foodelivery.data.remote.dto.OrderDto
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.repository.IOrderRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val db: AppDatabase
) : IOrderRepository {

    override suspend fun placeOrder(order: Order): Resource<String> {
        return try {
            val ref = firestore.collection("orders").document()
            val dto = order.toDto().copy(id = ref.id)
            ref.set(dto).await()
            db.cartDao().clearCart()
            Resource.Success(ref.id)
        } catch(e: Exception) { Resource.Error(e.message?:"Error") }
    }

    override fun getOrderHistory(userId: String): Flow<Resource<List<Order>>> = callbackFlow {
        val q = firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val l = q.addSnapshotListener { s, e ->
            if(e != null) { trySend(Resource.Error(e.message?:"Error")); return@addSnapshotListener }
            val list = s?.toObjects(OrderDto::class.java)?.map { it.toDomain() } ?: emptyList()
            trySend(Resource.Success(list))
        }
        awaitClose { l.remove() }
    }

    override fun getAllOrders(): Flow<Resource<List<Order>>> = callbackFlow {
        val q = firestore.collection("orders")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val l = q.addSnapshotListener { s, e ->
            if(e != null) {
                trySend(Resource.Error(e.message ?: "Lỗi tải danh sách đơn hàng"))
                return@addSnapshotListener
            }
            val list = s?.toObjects(OrderDto::class.java)?.map { it.toDomain() } ?: emptyList()
            trySend(Resource.Success(list))
        }
        awaitClose { l.remove() }
    }

    override suspend fun updateOrderStatus(orderId: String, status: String, driverId: String?): Resource<Boolean> {
        return try {
            val map = mutableMapOf<String, Any>("status" to status)
            if(driverId != null) map["driverId"] = driverId
            firestore.collection("orders").document(orderId).update(map).await()
            Resource.Success(true)
        } catch(e: Exception) { Resource.Error(e.message?:"Error") }
    }

    override suspend fun getOrderDetail(orderId: String): Resource<Order> {
        return try {
            val snap = firestore.collection("orders").document(orderId).get().await()
            val dto = snap.toObject(OrderDto::class.java) ?: throw Exception("Not Found")
            Resource.Success(dto.toDomain())
        } catch(e: Exception) { Resource.Error(e.message?:"Error") }
    }

    override suspend fun cancelOrder(orderId: String): Resource<Boolean> = updateOrderStatus(orderId, "CANCELLED", null)
}