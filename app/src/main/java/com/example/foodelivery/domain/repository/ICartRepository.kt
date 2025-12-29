package com.example.foodelivery.domain.repository

import com.example.foodelivery.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface ICartRepository {
    fun getCart(): Flow<List<CartItem>>
    suspend fun addToCart(item: CartItem)
    suspend fun removeFromCart(foodId: String)
    suspend fun updateQuantity(foodId: String, newQuantity: Int)
    suspend fun clearCart()
}