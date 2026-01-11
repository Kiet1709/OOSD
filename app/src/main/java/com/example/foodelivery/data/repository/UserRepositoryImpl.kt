package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.dao.UserDao
import com.example.foodelivery.data.mapper.toDomain
import com.example.foodelivery.data.mapper.toDto
import com.example.foodelivery.data.mapper.toEntity
import com.example.foodelivery.data.remote.dto.UserDto
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.domain.repository.IUserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userDao: UserDao
) : IUserRepository {

    override suspend fun saveUserInfo(user: User): Resource<Boolean> {
        return try {
            firestore.collection("users").document(user.id).set(user.toDto()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Lỗi lưu Database")
        }
    }

    override fun getUser(): Flow<User?> = callbackFlow {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }

        val listener = firestore.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val userDto = snapshot.toObject(UserDto::class.java)
                    trySend(userDto?.toDomain())
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.id).set(user.toDto()).await()
            userDao.insertUser(user.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        delay(100)
    }

    override suspend fun getUser(uid: String): Resource<User> {
        return try {
            val localUser = userDao.getUserById(uid)
            if (localUser != null) {
                return Resource.Success(localUser.toDomain())
            }

            val userDto = firestore.collection("users").document(uid).get().await().toObject(UserDto::class.java)
            if (userDto != null) {
                userDao.insertUser(userDto.toEntity())
                Resource.Success(userDto.toDomain())
            } else {
                Resource.Error("Không tìm thấy thông tin người dùng")
            }
        } catch (e: Exception) {
            val offlineUser = userDao.getUserById(uid)
            if (offlineUser != null) {
                return Resource.Success(offlineUser.toDomain())
            }
            Resource.Error(e.message ?: "Lỗi kết nối")
        }
    }

    override suspend fun updateProfile(uid: String, name: String, phone: String, address: String, avatarUrl: String?, coverPhotoUrl: String?): Resource<Boolean> {
        return try {
            val updates = mutableMapOf<String, Any>()
            updates["name"] = name
            updates["phoneNumber"] = phone
            updates["address"] = address
            if (avatarUrl != null) {
                updates["avatarUrl"] = avatarUrl
            }
            if (coverPhotoUrl != null) {
                updates["coverPhotoUrl"] = coverPhotoUrl
            }

            firestore.collection("users").document(uid).update(updates).await()

            val cachedUser = userDao.getUserById(uid)
            if (cachedUser != null) {
                val updatedUser = cachedUser.copy(
                    name = name,
                    phoneNumber = phone,
                    address = address,
                    avatarUrl = avatarUrl ?: cachedUser.avatarUrl,
                    coverPhotoUrl = coverPhotoUrl ?: cachedUser.coverPhotoUrl
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
            val userDto = firestore.collection("users").document(userId).get().await().toObject(UserDto::class.java)
            userDto?.toDomain()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
