package com.example.foodelivery.data.local.dao
import androidx.room.*;
import com.example.foodelivery.data.local.entity.UserEntity;
import kotlinx.coroutines.flow.Flow

@Dao interface UserDao {
    @Query("SELECT * FROM users LIMIT 1") fun getUser(): Flow<UserEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveUser(user: UserEntity)
    @Query("DELETE FROM users") suspend fun clearUser()
}