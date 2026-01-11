package com.example.foodelivery.domain.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.User

interface IAuthRepository {
    suspend fun login(email: String, pass: String): Resource<User>
    suspend fun register(name: String, email: String, pass: String, phone: String, role: String): Resource<User> // Added role
    suspend fun logout()
    suspend fun getCurrentUser(): User?
    suspend fun sendPasswordResetEmail(email: String): Resource<Boolean>
}