package com.example.foodelivery.data.local.dao
import androidx.room.*;
import com.example.foodelivery.data.local.entity.FoodEntity;
import kotlinx.coroutines.flow.Flow

@Dao interface FoodDao {
    @Query("SELECT * FROM foods") fun getAllFoods(): Flow<List<FoodEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertFoods(foods: List<FoodEntity>)
    @Query("DELETE FROM foods") suspend fun clearAll()
    @Transaction suspend fun refreshFoods(foods: List<FoodEntity>) { clearAll(); insertFoods(foods) }
    @Query("SELECT * FROM foods WHERE id = :id") suspend fun getFoodById(id: String): FoodEntity?
    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%'") suspend fun searchFood(query: String): List<FoodEntity>
    @Query("SELECT * FROM foods WHERE categoryId = :type") suspend fun getFoodsByType(type: String): List<FoodEntity>
}