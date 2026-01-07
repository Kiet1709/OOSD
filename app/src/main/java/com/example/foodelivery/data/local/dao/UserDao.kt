package com.example.foodelivery.data.local.dao

import androidx.room.*
import com.example.foodelivery.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    // Alias cho insertUser để tương thích với các repository cũ nếu cần
    @Transaction
    suspend fun saveUser(user: UserEntity) {
        insertUser(user)
    }
    
    @Query("DELETE FROM users")
    suspend fun clearUser()
}