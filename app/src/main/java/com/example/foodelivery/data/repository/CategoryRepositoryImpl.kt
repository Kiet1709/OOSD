package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.AppDatabase
import com.example.foodelivery.data.mapper.*
import com.example.foodelivery.data.remote.dto.CategoryDto
import com.example.foodelivery.domain.model.Category
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val firestore: FirebaseFirestore
) : ICategoryRepository {

    private val dao = db.categoryDao()

    // Flag để đảm bảo chỉ check seed data 1 lần mỗi khi mở app
    private var hasCheckedSeedData = false

    override fun getCategories(): Flow<Resource<List<Category>>> = callbackFlow {
        // 1. Emit Local
        val localJob = launch {
            dao.getAllCategories().collect { entities ->
                val list = entities.map { it.toDomainModel() }
                trySend(Resource.Success(list))
            }
        }

        // 2. Sync Firestore
        val listener = firestore.collection("categories")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    trySend(Resource.Error(e.message ?: "Lỗi tải danh mục"))
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    // [AUTO-SEED FIX]: Kiểm tra thiếu danh mục nào thì bù danh mục đó
                    // Không phụ thuộc vào việc list có rỗng hay không
                    if (!hasCheckedSeedData) {
                        hasCheckedSeedData = true
                        val currentNames = snapshot.documents.mapNotNull { it.getString("name") }
                        launch { seedMissingCategories(currentNames) }
                    }

                    val remoteCategories = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(CategoryDto::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }

                    launch {
                        dao.replaceCategories(remoteCategories.map { it.toEntity() })
                    }
                }
            }

        awaitClose {
            localJob.cancel()
            listener.remove()
        }
    }

    // Hàm bổ sung danh mục còn thiếu
    private suspend fun seedMissingCategories(existingNames: List<String>) {
        val defaults = listOf(
            Category(id = "", name = "Món chính", imageUrl = "https://cdn-icons-png.flaticon.com/512/2934/2934108.png"),
            Category(id = "", name = "Đồ ăn nhanh", imageUrl = "https://cdn-icons-png.flaticon.com/512/737/737967.png"),
            Category(id = "", name = "Đồ uống", imageUrl = "https://cdn-icons-png.flaticon.com/512/2738/2738730.png"),
            Category(id = "", name = "Tráng miệng", imageUrl = "https://cdn-icons-png.flaticon.com/512/992/992717.png"),
            Category(id = "", name = "Salad", imageUrl = "https://cdn-icons-png.flaticon.com/512/2515/2515127.png")
        )

        defaults.forEach { category ->
            // Chỉ thêm nếu tên chưa tồn tại
            if (category.name !in existingNames) {
                try {
                    addCategory(category)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override suspend fun getCategoryById(id: String): Resource<Category> {
        return try {
             val snap = firestore.collection("categories").document(id).get().await()
             val dto = snap.toObject(CategoryDto::class.java)
             if (dto != null) {
                 val finalDto = dto.copy(id = snap.id)
                 Resource.Success(finalDto.toEntity().toDomainModel())
             } else {
                 Resource.Error("Không tìm thấy danh mục")
             }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi tải chi tiết danh mục")
        }
    }

    override suspend fun addCategory(category: Category): Resource<Boolean> {
        return try {
            val ref = if (category.id.isEmpty()) firestore.collection("categories").document() 
                      else firestore.collection("categories").document(category.id)
            
            val dto = category.toDto().copy(id = ref.id)
            ref.set(dto).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi thêm danh mục")
        }
    }

    override suspend fun updateCategory(category: Category): Resource<Boolean> {
        return addCategory(category)
    }

    override suspend fun deleteCategory(id: String): Resource<Boolean> {
        return try {
            firestore.collection("categories").document(id).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi xóa danh mục")
        }
    }
}