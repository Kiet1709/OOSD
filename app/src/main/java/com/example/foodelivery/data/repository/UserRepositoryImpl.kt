package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.dao.UserDao
import com.example.foodelivery.data.mapper.toDomain
import com.example.foodelivery.data.mapper.toEntity
import com.example.foodelivery.data.remote.dto.UserDto
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
    private val firestore: FirebaseFirestore , // Inject Firestore
    private val userDao: UserDao // [2] BẮT BUỘC PHẢI CÓ DAO ĐỂ LƯU VÀO MÁY
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
            // [QUAN TRỌNG] Lưu vào máy
            userDao.insertUser(user.toEntity())

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        // Xử lý logout (clear data, signout auth...)
        delay(100)
    }


    // --- HÀM 1: LẤY USER (CÓ CACHE OFFLINE) ---
    override suspend fun getUser(uid: String): Resource<User> {
        return try {
            // A. Tìm trong máy trước (Nhanh, không tốn mạng)
            val localUser = userDao.getUserById(uid)
            if (localUser != null) {
                // Nếu có, trả về ngay (Đã bao gồm Address nhờ Mapper bạn sửa)
                return Resource.Success(localUser.toDomain())
            }

            // B. Nếu máy chưa có -> Lên Firebase tải
            val snapshot = firestore.collection("users").document(uid).get().await()
            val userDto = snapshot.toObject(UserDto::class.java)

            if (userDto != null) {
                // C. Tải xong thì lưu ngay vào máy để lần sau dùng
                userDao.insertUser(userDto.toEntity())
                Resource.Success(userDto.toDomain())
            } else {
                Resource.Error("Không tìm thấy thông tin người dùng")
            }
        } catch (e: Exception) {
            // Nếu lỗi mạng mà trong máy có dữ liệu cũ -> Trả về dữ liệu cũ luôn (Offline mode)
            val offlineUser = userDao.getUserById(uid)
            if (offlineUser != null) {
                return Resource.Success(offlineUser.toDomain())
            }
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }


    // [THÊM ĐOẠN NÀY VÀO TRONG CLASS UserRepositoryImpl]:
    override suspend fun updateProfile(uid: String, name: String, phone: String, address: String): Resource<Boolean> {
        return try {
            // 1. Cập nhật lên Firebase (Chỉ update các trường thay đổi)
            val updates = mapOf(
                "name" to name,
                "phoneNumber" to phone,
                "address" to address
            )
            firestore.collection("users").document(uid).update(updates).await()

            // 2. Cập nhật vào Cache máy (Local DB) để đồng bộ
            // (Nếu bạn chưa inject UserDao thì tạm thời bỏ qua đoạn userDao bên dưới cũng được, nhưng nên làm để chuẩn)
            val cachedUser = userDao.getUserById(uid)
            if (cachedUser != null) {
                val updatedUser = cachedUser.copy(
                    name = name,
                    phoneNumber = phone,
                    address = address
                )
                userDao.insertUser(updatedUser)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Update failed")
        }
    }
    override suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            if (snapshot.exists()) {
                snapshot.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}