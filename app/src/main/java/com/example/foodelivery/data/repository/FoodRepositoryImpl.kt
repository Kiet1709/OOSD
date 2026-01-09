package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.FoodDatabase
import com.example.foodelivery.data.local.entity.FoodEntity
import com.example.foodelivery.data.mapper.*
import com.example.foodelivery.data.remote.dto.FoodDto
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val db: FoodDatabase,
    private val firestore: FirebaseFirestore
) : IFoodRepository {

    private val dao = db.foodDao()

    // ✅ LẤY MENU (REALTIME)
    override fun getMenu(): Flow<Resource<List<Food>>> = channelFlow {
        send(Resource.Loading())

        // 1. Theo dõi Room (Offline-first)
        val localJob = launch {
            dao.getAllFoods().collect { entities ->
                send(Resource.Success(entities.map { it.toDomain() }))
            }
        }

        try {
            // 2. Lấy từ Firebase - ✅ SỬA: Dùng isAvailable thay vì available
            val snap = firestore.collection("foods")
                .whereEqualTo("isAvailable", true)
                .get()
                .await()

            // 3. Parse dữ liệu
            val entities = snap.documents.mapNotNull { doc ->
                try {
                    val dto = doc.toObject(FoodDto::class.java)
                    dto?.let {
                        FoodEntity(
                            id = doc.id,
                            name = it.name ?: "",
                            description = it.description ?: "",
                            price = it.price ?: 0.0,
                            imageUrl = it.imageUrl ?: "",
                            categoryId = it.categoryId ?: "",
                            rating = it.rating ?: 0.0,
                            isAvailable = it.isAvailable ?: true
                        )
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FoodRepo", "Parse error: ${e.message}")
                    null
                }
            }

            // 4. Cập nhật SQLite
            dao.refreshFoods(entities)
            android.util.Log.d("FoodRepo", "✅ Loaded ${entities.size} foods from Firebase")
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("FoodRepo", "❌ Error: ${e.message}")
            send(Resource.Error(e.message ?: "Lỗi kết nối"))
        }
    }

    // ✅ LẤY CHI TIẾT MÓN
    override suspend fun getFoodDetail(id: String): Resource<Food> {
        // 1. Thử local trước
        val local = dao.getFoodById(id)
        if (local != null) {
            android.util.Log.d("FoodRepo", "✅ Food found in local: ${local.name}")
            return Resource.Success(local.toDomain())
        }

        // 2. Lấy từ Firebase
        return try {
            val snap = firestore.collection("foods")
                .document(id)
                .get()
                .await()

            val dto = snap.toObject(FoodDto::class.java)
                ?: throw Exception("Không tìm thấy món ăn")

            // Lưu vào local
            val entity = FoodEntity(
                id = snap.id,
                name = dto.name ?: "",
                description = dto.description ?: "",
                price = dto.price ?: 0.0,
                imageUrl = dto.imageUrl ?: "",
                categoryId = dto.categoryId ?: "",
                rating = dto.rating ?: 0.0,
                isAvailable = dto.isAvailable ?: true
            )
            dao.insertFood(entity)

            android.util.Log.d("FoodRepo", "✅ Food loaded from Firebase: ${entity.name}")
            Resource.Success(entity.toDomain())
        } catch (e: Exception) {
            android.util.Log.e("FoodRepo", "❌ Error: ${e.message}")
            Resource.Error(e.message ?: "Lỗi tải chi tiết")
        }
    }

    // ✅ LẤY MÓN THEO TYPE
    override suspend fun getFoodsByType(type: String): List<Food> {
        return try {
            when (type) {
                "popular" -> {
                    dao.getAllFoods().first()
                        .sortedByDescending { it.rating }
                        .take(10)
                        .map { it.toDomain() }
                }
                "recommended" -> {
                    dao.getAllFoods().first()
                        .shuffled()
                        .take(10)
                        .map { it.toDomain() }
                }
                else -> {
                    // Type là categoryId
                    getFoodsByCategory(type)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("FoodRepo", "❌ Error getFoodsByType: ${e.message}")
            emptyList()
        }
    }

    // ✅ LẤY MÓN THEO CATEGORY
    override suspend fun getFoodsByCategory(categoryId: String): List<Food> {
        return try {
            // 1. Thử local trước
            val localFoods = dao.getAllFoods().first()
                .filter { it.categoryId == categoryId }
                .map { it.toDomain() }

            if (localFoods.isNotEmpty()) {
                android.util.Log.d("FoodRepo", "✅ Found ${localFoods.size} foods in category $categoryId (local)")
                return localFoods
            }

            // 2. Lấy từ Firebase
            val snap = firestore.collection("foods")
                .whereEqualTo("categoryId", categoryId)
                .whereEqualTo("isAvailable", true)
                .get()
                .await()

            val foods = snap.documents.mapNotNull { doc ->
                try {
                    val dto = doc.toObject(FoodDto::class.java)
                    dto?.let {
                        FoodEntity(
                            id = doc.id,
                            name = it.name ?: "",
                            description = it.description ?: "",
                            price = it.price ?: 0.0,
                            imageUrl = it.imageUrl ?: "",
                            categoryId = it.categoryId ?: "",
                            rating = it.rating ?: 0.0,
                            isAvailable = it.isAvailable ?: true
                        ).toDomain()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("FoodRepo", "Parse error: ${e.message}")
                    null
                }
            }

            android.util.Log.d("FoodRepo", "✅ Found ${foods.size} foods in category $categoryId (Firebase)")
            foods
        } catch (e: Exception) {
            android.util.Log.e("FoodRepo", "❌ Error: ${e.message}")
            emptyList()
        }
    }

    // ✅ THÊM MÓN (ADMIN)
    override suspend fun addFood(food: Food): Resource<Boolean> {
        return try {
            val dto = food.toDto()
            val ref = if (dto.id.isEmpty()) {
                firestore.collection("foods").document()
            } else {
                firestore.collection("foods").document(dto.id)
            }

            ref.set(dto.copy(id = ref.id)).await()
            android.util.Log.d("FoodRepo", "✅ Added food: ${food.name}")
            Resource.Success(true)
        } catch (e: Exception) {
            android.util.Log.e("FoodRepo", "❌ Error: ${e.message}")
            Resource.Error(e.message ?: "Lỗi thêm món")
        }
    }

    // ✅ CẬP NHẬT MÓN (ADMIN)
    override suspend fun updateFood(food: Food): Resource<Boolean> {
        return try {
            val dto = food.toDto()
            firestore.collection("foods")
                .document(food.id)
                .set(dto)
                .await()

            android.util.Log.d("FoodRepo", "✅ Updated food: ${food.name}")
            Resource.Success(true)
        } catch (e: Exception) {
            android.util.Log.e("FoodRepo", "❌ Error: ${e.message}")
            Resource.Error(e.message ?: "Lỗi cập nhật")
        }
    }

    // ✅ XÓA MÓN (ADMIN)
    override suspend fun deleteFood(id: String): Resource<Boolean> {
        return try {
            firestore.collection("foods")
                .document(id)
                .delete()
                .await()

            android.util.Log.d("FoodRepo", "✅ Deleted food: $id")
            Resource.Success(true)
        } catch (e: Exception) {
            android.util.Log.e("FoodRepo", "❌ Error: ${e.message}")
            Resource.Error(e.message ?: "Lỗi xóa")
        }
    }

    // ✅ CẬP NHẬT TRẠNG THÁI
    override suspend fun updateFoodStatus(id: String, isAvailable: Boolean): Resource<Boolean> {
        return try {
            firestore.collection("foods")
                .document(id)
                .update("isAvailable", isAvailable)
                .await()

            android.util.Log.d("FoodRepo", "✅ Updated status: $id → $isAvailable")
            Resource.Success(true)
        } catch (e: Exception) {
            android.util.Log.e("FoodRepo", "❌ Error: ${e.message}")
            Resource.Error(e.message ?: "Lỗi cập nhật")
        }
    }

    // ✅ TÌM KIẾM
    override suspend fun searchFood(query: String): List<Food> {
        return dao.searchFood(query).map { it.toDomain() }
    }
}