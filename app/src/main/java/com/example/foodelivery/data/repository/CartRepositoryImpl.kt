package com.example.foodelivery.data.repository

import com.example.foodelivery.data.local.FoodDatabase
import com.example.foodelivery.data.mapper.toDomain
import com.example.foodelivery.data.mapper.toEntity
import com.example.foodelivery.domain.model.CartItem
import com.example.foodelivery.domain.repository.ICartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    db: FoodDatabase
) : ICartRepository {

    private val dao = db.cartDao()

    override fun getCart(): Flow<List<CartItem>> {
        return dao.getCart().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addToCart(item: CartItem) {
        // [SENIOR LOGIC]: Kiểm tra tồn tại để cộng dồn
        val existingItem = dao.getCartItemById(item.foodId)

        if (existingItem != null) {
            // Logic: Số lượng mới = Số lượng cũ + Số lượng thêm vào
            val newQuantity = existingItem.quantity + item.quantity
            dao.updateQuantity(item.foodId, newQuantity)
        } else {
            // Chưa có -> Insert mới
            dao.addToCart(item.toEntity())
        }
    }

    override suspend fun removeFromCart(foodId: String) {
        dao.removeFromCart(foodId)
    }

    override suspend fun updateQuantity(foodId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            // Nếu giảm về 0 hoặc âm -> Xóa luôn khỏi giỏ
            dao.removeFromCart(foodId)
        } else {
            dao.updateQuantity(foodId, newQuantity)
        }
    }

    override suspend fun clearCart() {
        dao.clearCart()
    }
}