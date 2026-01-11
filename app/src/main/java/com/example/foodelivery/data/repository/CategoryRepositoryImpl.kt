package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Category
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ICategoryRepository {

    override fun getCategories(): Flow<Resource<List<Category>>> = callbackFlow {
        trySend(Resource.Loading())

        val listener = firestore.collection("categories").addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(Resource.Error(error.localizedMessage ?: "An unexpected error occurred"))
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val categories = snapshot.toObjects(Category::class.java)
                trySend(Resource.Success(categories))
            } else {
                trySend(Resource.Error("No data found"))
            }
        }

        awaitClose { listener.remove() }
    }

    override suspend fun getCategoryById(id: String): Resource<Category> {
        return try {
            val snapshot = firestore.collection("categories").document(id).get().await()
            val category = snapshot.toObject(Category::class.java)
            if (category != null) {
                Resource.Success(category)
            } else {
                Resource.Error("Category not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun addCategory(category: Category): Resource<Boolean> {
        return try {
            // Firestore will auto-generate an ID
            val docRef = firestore.collection("categories").document()
            val newCategory = category.copy(id = docRef.id)
            docRef.set(newCategory).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add category")
        }
    }

    override suspend fun updateCategory(category: Category): Resource<Boolean> {
        return try {
            firestore.collection("categories").document(category.id).set(category).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update category")
        }
    }

    override suspend fun deleteCategory(id: String): Resource<Boolean> {
        return try {
            firestore.collection("categories").document(id).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete category")
        }
    }
}