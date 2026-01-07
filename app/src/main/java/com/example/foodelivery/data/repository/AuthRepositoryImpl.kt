package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.AppDatabase
import com.example.foodelivery.data.mapper.toDomain
import com.example.foodelivery.data.mapper.toEntity
import com.example.foodelivery.data.remote.dto.UserDto
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val db: AppDatabase
) : IAuthRepository {

    override suspend fun login(email: String, pass: String): Resource<User> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            val uid = auth.currentUser?.uid ?: throw Exception("UID Null")
            
            // Fetch mới nhất từ Firestore
            val snap = firestore.collection("users").document(uid).get().await()
            val dto = snap.toObject(UserDto::class.java) ?: throw Exception("User Null")
            val user = dto.toEntity().toDomain()
            
            // Cache vào Local
            db.userDao().insertUser(user.toEntity())
            
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Đăng nhập thất bại")
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
                role = role
            )
            firestore.collection("users").document(uid).set(dto).await()
            
            val user = dto.toEntity().toDomain()
            db.userDao().insertUser(user.toEntity())
            
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Đăng ký thất bại")
        }
    }

    override suspend fun logout() {
        auth.signOut()
        db.userDao().clearUser()
        db.cartDao().clearCart()
        db.orderDao().clearOrders()
    }

    override suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        // Ưu tiên lấy từ Firestore để đảm bảo Role đúng nhất
        return try {
            val snap = firestore.collection("users").document(uid).get().await()
            val user = snap.toObject(UserDto::class.java)?.toEntity()?.toDomain()
            // Sync lại local nếu lấy được
            if (user != null) {
                db.userDao().insertUser(user.toEntity())
            }
            user
        } catch (e: Exception) {
            // Fallback về local nếu mất mạng
            db.userDao().getUser().first()?.toDomain()
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi gửi email")
        }
    }
}