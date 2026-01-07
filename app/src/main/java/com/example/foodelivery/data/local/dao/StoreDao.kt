package com.example.foodelivery.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodelivery.data.local.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM store_info WHERE id = 1")
    fun getStoreInfo(): Flow<StoreEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStoreInfo(info: StoreEntity)
}