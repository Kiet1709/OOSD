package com.example.foodelivery.data.repository

import android.util.Log
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.mapper.toDto
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : IFoodRepository {

    override fun getMenu(): Flow<Resource<List<Food>>> = callbackFlow {
        trySend(Resource.Loading())
        val listener = firestore.collection("foods")
            .whereEqualTo("available", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An unexpected error occurred"))
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(Resource.Error("No data found"))
                    return@addSnapshotListener
                }

                val foods = snapshot.documents.mapNotNull { doc ->
                    try {
                        Food(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            price = (doc.get("price") as? Number)?.toDouble() ?: 0.0,
                            imageUrl = doc.getString("imageUrl") ?: "",
                            categoryId = doc.getString("categoryId") ?: "",
                            restaurantId = doc.getString("restaurantId") ?: "",
                            rating = (doc.get("rating") as? Number)?.toDouble() ?: 0.0,
                            isAvailable = doc.getBoolean("available") ?: false
                        )
                    } catch (e: Exception) {
                        Log.e("FoodRepository", "Failed to parse food document ${doc.id}", e)
                        null // Skip this document if parsing fails
                    }
                }
                trySend(Resource.Success(foods))
            }
        awaitClose { listener.remove() }
    }

    override fun getMenuByRestaurantId(restaurantId: String): Flow<Resource<List<Food>>> = callbackFlow {
        trySend(Resource.Loading())
        val listener = firestore.collection("foods")
            .whereEqualTo("restaurantId", restaurantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "An unexpected error occurred"))
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    trySend(Resource.Error("No data found for this restaurant"))
                    return@addSnapshotListener
                }

                val foods = snapshot.documents.mapNotNull { doc ->
                    try {
                        Food(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            price = (doc.get("price") as? Number)?.toDouble() ?: 0.0,
                            imageUrl = doc.getString("imageUrl") ?: "",
                            categoryId = doc.getString("categoryId") ?: "",
                            restaurantId = doc.getString("restaurantId") ?: "",
                            rating = (doc.get("rating") as? Number)?.toDouble() ?: 0.0,
                            isAvailable = doc.getBoolean("available") ?: false
                        )
                    } catch (e: Exception) {
                        Log.e("FoodRepository", "Failed to parse food document ${doc.id}", e)
                        null // Skip this document
                    }
                }
                trySend(Resource.Success(foods))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getFoodDetail(id: String): Resource<Food> {
        return try {
            val doc = firestore.collection("foods").document(id).get().await()
            if (doc.exists()) {
                val food = Food(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    price = (doc.get("price") as? Number)?.toDouble() ?: 0.0,
                    imageUrl = doc.getString("imageUrl") ?: "",
                    categoryId = doc.getString("categoryId") ?: "",
                    restaurantId = doc.getString("restaurantId") ?: "",
                    rating = (doc.get("rating") as? Number)?.toDouble() ?: 0.0,
                    isAvailable = doc.getBoolean("available") ?: false
                )
                Resource.Success(food)
            } else {
                Resource.Error("Food not found")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error loading food details")
        }
    }

    override suspend fun addFood(food: Food): Resource<Boolean> {
        return try {
            val ref = firestore.collection("foods").document()
            ref.set(food.copy(id = ref.id).toDto()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error adding food")
        }
    }

    override suspend fun updateFood(food: Food): Resource<Boolean> {
        return try {
            firestore.collection("foods").document(food.id).set(food.toDto()).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating food")
        }
    }

    override suspend fun deleteFood(id: String): Resource<Boolean> {
        return try {
            firestore.collection("foods").document(id).delete().await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error deleting food")
        }
    }

    override suspend fun getFoodsByCategory(categoryId: String): List<Food> {
        return try {
            val snapshot = firestore.collection("foods")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    Food(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        price = (doc.get("price") as? Number)?.toDouble() ?: 0.0,
                        imageUrl = doc.getString("imageUrl") ?: "",
                        categoryId = doc.getString("categoryId") ?: "",
                        restaurantId = doc.getString("restaurantId") ?: "",
                        rating = (doc.get("rating") as? Number)?.toDouble() ?: 0.0,
                        isAvailable = doc.getBoolean("available") ?: false
                    )
                } catch (e: Exception) {
                    Log.e("FoodRepository", "Failed to parse food document ${doc.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FoodRepository", "Error fetching foods by category", e)
            emptyList()
        }
    }
    
    override suspend fun getFoodsByType(type: String): List<Food> = emptyList()
    override suspend fun updateFoodStatus(id: String, isAvailable: Boolean): Resource<Boolean> = Resource.Success(true)
    override suspend fun searchFood(query: String): List<Food> = emptyList()
}