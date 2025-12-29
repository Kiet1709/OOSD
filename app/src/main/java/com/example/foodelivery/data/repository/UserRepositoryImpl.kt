package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore // Inject Firestore
) : IUserRepository {

    // [SENIOR LOGIC]: Lưu User vào Firestore
    override suspend fun saveUserInfo(user: User): Resource<Boolean> {
        return try {
            // Collection "users" -> Document ID = UserID (Auth UID)
            firestore.collection("users")
                .document(user.id)
                .set(user)
                .await() // Sử dụng await() từ thư viện coroutines-play-services

            Resource.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Lỗi lưu Database")
        }
    }

    // --- CÁC HÀM KHÁC GIỮ NGUYÊN HOẶC NÂNG CẤP NHẸ ---

    override fun getUser(): Flow<User?> = callbackFlow {
        // 1. Lấy ID của người đang đăng nhập
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            // 2. Lắng nghe thay đổi từ Firestore (Realtime)
            val listener = firestore.collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error) // Nếu lỗi thì đóng flow
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        // Convert dữ liệu Firestore sang User Object
                        val user = snapshot.toObject(User::class.java)
                        trySend(user) // Bắn dữ liệu mới về ViewModel
                    } else {
                        trySend(null) // Không tìm thấy user
                    }
                }

            // Đảm bảo hủy listener khi không dùng nữa để tránh rò rỉ bộ nhớ
            awaitClose { listener.remove() }
        } else {
            trySend(null) // Chưa đăng nhập
            awaitClose { }
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        // Xử lý logout (clear data, signout auth...)
        delay(100)
    }
}