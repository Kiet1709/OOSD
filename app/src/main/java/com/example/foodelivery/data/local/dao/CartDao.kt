package com.example.foodelivery.data.local.dao
import androidx.room.*;
import com.example.foodelivery.data.local.entity.CartEntity;
import kotlinx.coroutines.flow.Flow

@Dao interface CartDao {
    @Query("SELECT * FROM cart") fun getCart(): Flow<List<CartEntity>>
    // [SENIOR]: Cần hàm này để check xem món đã có chưa -> Cộng dồn số lượng
    @Query("SELECT * FROM cart WHERE foodId = :foodId LIMIT 1")
    suspend fun getCartItemById(foodId: String): CartEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun addToCart(item: CartEntity)
    @Query("DELETE FROM cart WHERE foodId = :foodId") suspend fun removeFromCart(foodId: String)
    @Query("UPDATE cart SET quantity = :qty WHERE foodId = :id") suspend fun updateQuantity(id: String, qty: Int)
    @Query("DELETE FROM cart") suspend fun clearCart()
}