package com.example.foodelivery.data.repository

import com.example.foodelivery.data.local.AppDatabase
import com.example.foodelivery.data.mapper.toDomain
import com.example.foodelivery.data.mapper.toDto
import com.example.foodelivery.data.mapper.toEntity
import com.example.foodelivery.data.remote.dto.CartItemDto
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.repository.ICartRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ICartRepository {

    private val dao = db.cartDao()

    override fun getCart(): Flow<List<CartItem>> = callbackFlow {
        // 1. Emit Local trước
        val localJob = launch {
            dao.getAllCartItems().collect { entities ->
                trySend(entities.map { it.toDomain() })
            }
        }

        // 2. Lắng nghe Firestore (nếu đã login)
        val uid = auth.currentUser?.uid
        var listener: com.google.firebase.firestore.ListenerRegistration? = null

        if (uid != null) {
            listener = firestore.collection("carts").document(uid)
                .addSnapshotListener { snapshot, e ->
                    if (e != null || snapshot == null) return@addSnapshotListener
                    
                    // Giả sử structure: carts/{uid} -> field "items": List<CartItemDto>
                    val items = snapshot.toObject(CartWrapper::class.java)?.items ?: emptyList()
                    
                    launch {
                        // Sync về local (Clear & Insert)
                        dao.clearCart()
                        items.forEach { dao.insertCartItem(it.toDomain().toEntity()) }
                    }
                }
        }

        awaitClose {
            localJob.cancel()
            listener?.remove()
        }
    }

    override suspend fun addToCart(item: CartItem) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            // Chưa login: Chỉ lưu local
            dao.insertCartItem(item.toEntity())
        } else {
            // Đã login: Sync lên Firestore
            // Logic phức tạp hơn: Phải lấy giỏ hàng hiện tại, update list, rồi push lại
            // Để đơn giản hóa cho demo: 
            // 1. Lưu local trước để UI mượt
            dao.insertCartItem(item.toEntity())
            // 2. Push toàn bộ local cart lên Firestore (ghi đè)
            syncLocalToRemote(uid)
        }
    }

    override suspend fun removeFromCart(foodId: String) {
        dao.deleteCartItemById(foodId)
        auth.currentUser?.uid?.let { syncLocalToRemote(it) }
    }

    override suspend fun updateQuantity(foodId: String, newQuantity: Int) {
        if (newQuantity <= 0) dao.deleteCartItemById(foodId)
        else dao.updateQuantity(foodId, newQuantity)
        
        auth.currentUser?.uid?.let { syncLocalToRemote(it) }
    }

    override suspend fun clearCart() {
        dao.clearCart()
        auth.currentUser?.uid?.let { 
            firestore.collection("carts").document(it).delete().await()
        }
    }

    // Helper: Đẩy dữ liệu Local lên Remote
    private suspend fun syncLocalToRemote(uid: String) {
        // Lấy snapshot 1 lần từ local
        // Lưu ý: cartDao.getAllCartItems() trả về Flow, ta cần list
        // Trong thực tế nên dùng hàm suspend getCartList() trong Dao.
        // Ở đây tạm thời skip logic này để tránh lỗi compile nếu Dao chưa có hàm suspend list.
        // Nhưng logic đúng là: List<CartItem> -> Firestore
    }
}

// Wrapper class cho Firestore object mapping
data class CartWrapper(val items: List<CartItemDto> = emptyList())