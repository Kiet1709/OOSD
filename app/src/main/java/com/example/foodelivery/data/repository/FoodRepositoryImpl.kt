package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.dao.FoodDao
import com.example.foodelivery.data.mapper.toDomainModel
import com.example.foodelivery.data.mapper.toDto
import com.example.foodelivery.data.mapper.toEntity
import com.example.foodelivery.data.remote.dto.FoodDto
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val dao: FoodDao,
    private val firestore: FirebaseFirestore
) : IFoodRepository {

    override fun getMenu(): Flow<Resource<List<Food>>> = callbackFlow {
        // 1. Emit data từ Local DB trước (để hiện ngay lập tức - Offline first)
        val localJob = launch {
            dao.getAllFoods().collect { entities ->
                trySend(Resource.Success(entities.map { it.toDomainModel() }))
            }
        }

        // 2. Lắng nghe Realtime từ Firestore
        val listener = firestore.collection("foods")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error("Lỗi đồng bộ: ${error.message}"))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // [FIX]: Duyệt từng document để bắt lỗi dữ liệu từng item
                    // Nếu 1 item lỗi format, nó sẽ bị bỏ qua thay vì làm crash cả danh sách
                    val remoteFoods = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(FoodDto::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null // Bỏ qua item lỗi
                        }
                    }

                    launch {
                        // [FIX]: Dùng Transaction để xóa cũ + thêm mới an toàn
                        val entities = remoteFoods.map { it.toEntity() }
                        dao.replaceFoods(entities)
                    }
                }
            }

        awaitClose {
            localJob.cancel()
            listener.remove()
        }
    }

    override suspend fun addFood(food: Food): Resource<Boolean> {
        return try {
            val dto = food.toDto()
            // [FIX]: Đảm bảo ID rỗng thì tạo mới, có ID thì dùng ID đó
            val ref = if (dto.id.isEmpty()) firestore.collection("foods").document() 
                      else firestore.collection("foods").document(dto.id)
            
            val finalDto = dto.copy(id = ref.id)
            
            ref.set(finalDto).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi thêm món")
        }
    }

    override suspend fun getFoodDetail(id: String): Resource<Food> {
        // Ưu tiên lấy từ Local
        val local = dao.getFoodById(id)
        if (local != null) return Resource.Success(local.toDomainModel())

        // Nếu không có thì fetch từ Remote
        return try {
            val snap = firestore.collection("foods").document(id).get().await()
            val dto = snap.toObject(FoodDto::class.java)
            if (dto != null) {
                // Gán ID từ document ID để đảm bảo chính xác
                val finalDto = dto.copy(id = snap.id)
                Resource.Success(finalDto.toEntity().toDomainModel())
            } else {
                Resource.Error("Không tìm thấy món ăn")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi tải chi tiết")
        }
    }

    override suspend fun updateFood(food: Food): Resource<Boolean> {
        return try {
            if (food.id.isEmpty()) return Resource.Error("ID món ăn không hợp lệ")
            
            firestore.collection("foods").document(food.id).set(food.toDto()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi cập nhật")
        }
    }

    override suspend fun deleteFood(id: String): Resource<Boolean> {
        return try {
             if (id.isEmpty()) return Resource.Error("ID món ăn không hợp lệ")

            firestore.collection("foods").document(id).delete().await()
            // Listener sẽ tự clear local sau khi server xóa, 
            // nhưng gọi thêm delete local ở đây để UI phản hồi tức thì
            dao.deleteFoodById(id)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi xóa")
        }
    }

    override suspend fun updateFoodStatus(id: String, isAvailable: Boolean): Resource<Boolean> {
        return try {
            firestore.collection("foods").document(id).update("isAvailable", isAvailable).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi cập nhật trạng thái")
        }
    }

    override suspend fun searchFood(query: String): List<Food> {
        return dao.searchFoods(query).map { it.toDomainModel() }
    }

    override suspend fun getFoodsByType(type: String): List<Food> {
        return dao.getFoodsByType(type).map { it.toDomainModel() }
    }
}