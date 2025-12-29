package com.example.foodelivery.domain.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.User

interface IAuthRepository {
    suspend fun login(email: String, pass: String): Resource<User>
    suspend fun register(name: String, email: String, pass: String, phone: String): Resource<User>
    suspend fun logout()
    suspend fun getCurrentUser(): User?

    // ➕ THÊM HÀM NÀY ĐỂ LÀM CHỨC NĂNG QUÊN MẬT KHẨU
    suspend fun sendPasswordResetEmail(email: String): Resource<Boolean>
}