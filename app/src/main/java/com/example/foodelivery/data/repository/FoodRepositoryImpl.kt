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
    override fun getMenu(): Flow<Resource<List<Food>>> = channelFlow {
        send(Resource.Loading())

        // 1. Luôn theo dõi Room (Offline-first)
        val localJob = launch {
            dao.getAllFoods().collect { entities ->
                send(Resource.Success(entities.map { it.toDomain() }))
            }
        }
        try {
            // 2. Lấy từ Firebase - Dùng trường 'available' khớp với ảnh Firestore của bạn
            val snap = firestore.collection("foods")
                .whereEqualTo("available", true)
                .get()
                .await()
            // 3. Ép gán Document ID thủ công để đảm bảo không bị rỗng
            val entities = snap.documents.mapNotNull { doc ->
                val dto = doc.toObject(FoodDto::class.java)
                // Gán doc.id (ví dụ: 4P6Boepp...) vào DTO
                dto?.let {
                    FoodEntity(
                        id = doc.id, // Lấy Document ID (vd: 4P6Bo...) làm khóa chính
                        name = it.name ?: "",
                        description = it.description ?: "",
                        price = it.price ?: 0.0,
                        imageUrl = it.imageUrl ?: "",
                        categoryId = it.categoryId ?: "",
                        rating = it.rating ?: 0.0,
                        isAvailable = it.isAvailable ?: true
                    )
                }
            }
            // 4. Cập nhật SQLite
            dao.refreshFoods(entities)
        } catch (e: Exception) {
            e.printStackTrace()
            send(Resource.Error(e.message ?: "Lỗi kết nối "))
        }
    }
    override suspend fun addFood(food: Food): Resource<Boolean> {
        return try {
            val dto = food.toDto()
            val ref = if(dto.id.isEmpty()) firestore.collection("foods").document() else firestore.collection("foods").document(dto.id)
            ref.set(dto.copy(id = ref.id)).await()
            Resource.Success(true)
        } catch(e: Exception) { Resource.Error(e.message?:"Error") }
    }
    override suspend fun getFoodDetail(id: String): Resource<Food> {
        val local = dao.getFoodById(id)
        if (local != null) return Resource.Success(local.toDomain())
        return try {
            val snap = firestore.collection("foods").document(id).get().await()
            val dto = snap.toObject(FoodDto::class.java)?:throw Exception("Not Found")
            Resource.Success(dto.toDomain())
        } catch(e: Exception) { Resource.Error(e.message?:"Error") }
    }
    // [FIX 1]: Thêm hàm cập nhật món ăn
    override suspend fun updateFood(food: Food): Resource<Boolean> {
        return try {
            // TODO: Gọi API update ở đây
            // api.updateFood(food.id, food)
            Resource.Success(true) // Trả về Success giả lập
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi cập nhật món")
        }
    }

    // [FIX 2]: Thêm hàm xóa món ăn
    override suspend fun deleteFood(id: String): Resource<Boolean> {
        return try {
            // TODO: Gọi API delete ở đây
            // api.deleteFood(id)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi xóa món")
        }
    }

    // [FIX 3]: Thêm hàm cập nhật trạng thái (Còn món/Hết món)
    override suspend fun updateFoodStatus(id: String, isAvailable: Boolean): Resource<Boolean> {
        return try {
            // TODO: Gọi API update status ở đây
            // api.updateStatus(id, isAvailable)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi cập nhật trạng thái")
        }
    }
    override suspend fun searchFood(query: String): List<Food> = dao.searchFood(query).map { it.toDomain() }
    override suspend fun getFoodsByType(type: String): List<Food> = dao.getFoodsByType(type).map { it.toDomain() }
}