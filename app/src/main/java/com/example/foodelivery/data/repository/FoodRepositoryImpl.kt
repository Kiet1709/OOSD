package com.example.foodelivery.data.repository
import com.example.foodelivery.core.common.MockData
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.FoodDatabase
import com.example.foodelivery.data.local.entity.FoodEntity
import com.example.foodelivery.data.mapper.*
import com.example.foodelivery.data.remote.dto.FoodDto
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val db: FoodDatabase,
    private val firestore: FirebaseFirestore
) : IFoodRepository {
    private val dao = db.foodDao()
    private val isDemoMode = true 

    override fun getMenu(): Flow<Resource<List<Food>>> = channelFlow {
        send(Resource.Loading())

        if (isDemoMode) {
            getMockMenu().collect { send(it) }
        } else {
            // Real Mode: Theo dõi Local + Fetch Remote
            observeLocalMenu().collect { send(it) }
            fetchRemoteMenu()
        }
    }

    // --- HELPER FUNCTIONS (Chia nhỏ logic để Clean Code) ---

    private fun getMockMenu(): Flow<Resource<List<Food>>> = flow {
        delay(500) 
        emit(Resource.Success(MockData.foodList))
    }

    private fun observeLocalMenu(): Flow<Resource<List<Food>>> = dao.getAllFoods()
        .map { entities -> Resource.Success(entities.map { it.toDomain() }) }

    private suspend fun fetchRemoteMenu() {
        try {
            val snap = firestore.collection("foods")
                .whereEqualTo("available", true)
                .get()
                .await()
            
            val entities = snap.documents.mapNotNull { doc ->
                doc.toObject(FoodDto::class.java)?.let { dto ->
                    FoodEntity(
                        id = doc.id, 
                        name = dto.name ?: "",
                        description = dto.description ?: "",
                        price = dto.price ?: 0.0,
                        imageUrl = dto.imageUrl ?: "",
                        categoryId = dto.categoryId ?: "",
                        rating = dto.rating ?: 0.0,
                        isAvailable = dto.isAvailable ?: true
                    )
                }
            }
            dao.refreshFoods(entities)
        } catch (e: Exception) {
            e.printStackTrace()
            // Không gửi lỗi ra Flow chính để tránh ngắt UI đang hiển thị Local Data
        }
    }
    
    // --- CRUD OPERATIONS ---
    
    override suspend fun addFood(food: Food): Resource<Boolean> {
        return if (isDemoMode) {
            MockData.addFood(food)
            Resource.Success(true)
        } else {
            safeCall {
                val dto = food.toDto()
                val ref = if(dto.id.isEmpty()) firestore.collection("foods").document() else firestore.collection("foods").document(dto.id)
                ref.set(dto.copy(id = ref.id)).await()
                true
            }
        }
    }
    
    override suspend fun getFoodDetail(id: String): Resource<Food> {
        if (isDemoMode) {
            return MockData.getFoodById(id)?.let { Resource.Success(it) } 
                   ?: Resource.Error("Không tìm thấy món (Mock)")
        }

        val local = dao.getFoodById(id)
        if (local != null) return Resource.Success(local.toDomain())
        
        return safeCall {
            val snap = firestore.collection("foods").document(id).get().await()
            if (!snap.exists()) throw Exception("Món ăn không tồn tại")
            
            snap.toObject(FoodDto::class.java)?.copy(id = snap.id)?.toDomain() 
                ?: throw Exception("Lỗi dữ liệu")
        }
    }

    override suspend fun updateFood(food: Food): Resource<Boolean> {
        return if (isDemoMode) {
            MockData.updateFood(food)
            Resource.Success(true)
        } else {
            Resource.Success(true) // TODO: Implement Remote Update
        }
    }

    override suspend fun deleteFood(id: String): Resource<Boolean> {
        return if (isDemoMode) {
            MockData.deleteFood(id)
            Resource.Success(true)
        } else {
            Resource.Success(true) // TODO: Implement Remote Delete
        }
    }

    override suspend fun updateFoodStatus(id: String, isAvailable: Boolean): Resource<Boolean> {
         return Resource.Success(true)
    }
    
    override suspend fun searchFood(query: String): List<Food> {
        return if(isDemoMode) {
            MockData.foodList.filter { it.name.contains(query, ignoreCase = true) }
        } else {
            dao.searchFood(query).map { it.toDomain() }
        }
    }
    
    override suspend fun getFoodsByType(type: String): List<Food> = dao.getFoodsByType(type).map { it.toDomain() }

    // Utility để wrap try-catch cho gọn
    private suspend fun <T> safeCall(action: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(action())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi không xác định")
        }
    }
}