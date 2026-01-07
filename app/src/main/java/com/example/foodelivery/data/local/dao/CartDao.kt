package com.example.foodelivery.data.local.dao

import androidx.room.*
import com.example.foodelivery.data.local.entity.CartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartEntity)

    @Delete
    suspend fun deleteCartItem(item: CartEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    // --- Bổ sung các phương thức thiếu ---
    @Query("SELECT * FROM cart_items WHERE foodId = :foodId LIMIT 1")
    suspend fun getCartItemById(foodId: String): CartEntity?

    @Query("UPDATE cart_items SET quantity = :quantity WHERE foodId = :foodId")
    suspend fun updateQuantity(foodId: String, quantity: Int)

    @Query("DELETE FROM cart_items WHERE foodId = :foodId")
    suspend fun deleteCartItemById(foodId: String)
}