package com.example.foodelivery.data.local.dao
import androidx.room.*;
import com.example.foodelivery.data.local.entity.UserEntity;
import kotlinx.coroutines.flow.Flow

@Dao interface UserDao {
    @Query("SELECT * FROM users LIMIT 1") fun getUser(): Flow<UserEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveUser(user: UserEntity)
    @Query("DELETE FROM users") suspend fun clearUser()


    // 1. Lấy user theo ID cụ thể (Dùng để kiểm tra cache trước khi gọi Firebase)
    @Query("SELECT * FROM users WHERE id = :uid")
    suspend fun getUserById(uid: String): UserEntity?

    // 2. Lưu User (Tên hàm này khớp với UserRepositoryImpl mới)
    // (Chức năng y hệt saveUser, nhưng ta thêm để code repository không bị báo đỏ)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}