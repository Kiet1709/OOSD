package com.example.foodelivery.domain.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    // Lấy thông tin User hiện tại (Realtime Flow)
    fun getUser(): Flow<User?>

    // Cập nhật thông tin (Trả về Result để xử lý lỗi/thành công)
    suspend fun updateUser(user: User): Result<Unit>

    // Đăng xuất
    suspend fun logout()

    // [CODE MỚI - QUAN TRỌNG]: Hàm lưu thông tin User (Role, Name...) vào Firestore
    suspend fun saveUserInfo(user: User): Resource<Boolean>



    // 5. [THÊM MỚI - QUAN TRỌNG]: Hàm lấy User 1 lần (Có hỗ trợ Offline)
    suspend fun getUser(uid: String): Resource<User>


    suspend fun updateProfile(uid: String, name: String, phone: String, address: String): Resource<Boolean>
    suspend fun getUserById(userId: String): User?
}