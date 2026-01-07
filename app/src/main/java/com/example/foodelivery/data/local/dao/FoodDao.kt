package com.example.foodelivery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.foodelivery.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM foods")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getFoodById(id: String): FoodEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(foods: List<FoodEntity>)

    @Query("DELETE FROM foods WHERE id = :id")
    suspend fun deleteFoodById(id: String)

    @Query("DELETE FROM foods")
    suspend fun clearFoods()

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%'")
    suspend fun searchFoods(query: String): List<FoodEntity>

    @Query("SELECT * FROM foods WHERE categoryId = :type")
    suspend fun getFoodsByType(type: String): List<FoodEntity>

    // [FIX]: Thêm Transaction để đảm bảo tính toàn vẹn dữ liệu khi đồng bộ
    @Transaction
    suspend fun replaceFoods(foods: List<FoodEntity>) {
        clearFoods()
        insertFoods(foods)
    }
}