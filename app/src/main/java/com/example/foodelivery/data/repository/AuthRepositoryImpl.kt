package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.FoodDatabase
import com.example.foodelivery.data.mapper.*
import com.example.foodelivery.data.remote.dto.UserDto
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val db: FoodDatabase
) : IAuthRepository {

    override suspend fun login(email: String, pass: String): Resource<User> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            val uid = auth.currentUser?.uid ?: throw Exception("UID Null")
            val snap = firestore.collection("users").document(uid).get().await()
            val userDto = snap.toObject(UserDto::class.java) ?: throw Exception("User Null")
            val userEntity = userDto.toEntity()
            db.userDao().saveUser(userEntity)
            val userDomain = userEntity.toDomain()
            Resource.Success(userDomain)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("password") == true -> "Sai mật khẩu. Vui lòng thử lại."
                e.message?.contains("user-not-found") == true -> "Email chưa được đăng ký."
                e.message?.contains("incorrect") == true -> "Email hoặc mật khẩu không chính xác."
                else -> e.message ?: "Đã có lỗi xảy ra."
            }
            Resource.Error(errorMessage)
        }
    }

    override suspend fun register(name: String, email: String, pass: String, phone: String, role: String): Resource<User> {
        return try {
            val res = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = res.user?.uid ?: throw Exception("UID Null")
            val dto = UserDto(
                id = uid,
                name = name,
                email = email,
                phoneNumber = phone,
                role = role.uppercase() // Ensure role is consistent
            )
            firestore.collection("users").document(uid).set(dto).await()
            val userEntity = dto.toEntity()
            db.userDao().saveUser(userEntity)
            Resource.Success(userEntity.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error")
        }
    }

    override suspend fun logout() {
        auth.signOut()
        db.userDao().clearUser(); db.cartDao().clearCart(); db.orderDao().clearOrders()
    }

    override suspend fun getCurrentUser(): User? = db.userDao().getUser().first()?.toDomain()

    override suspend fun sendPasswordResetEmail(email: String): Resource<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi gửi email")
        }
    }
}