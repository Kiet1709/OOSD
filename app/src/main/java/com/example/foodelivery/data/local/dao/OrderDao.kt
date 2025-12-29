package com.example.foodelivery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodelivery.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    // Lọc lịch sử theo UserId cụ thể
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY timestamp DESC")
    fun getOrdersByUserId(userId: String): Flow<List<OrderEntity>>

    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}