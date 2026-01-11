package com.example.foodelivery.domain.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface IFoodRepository {
    // Client
    fun getMenu(): Flow<Resource<List<Food>>>
    suspend fun getFoodDetail(id: String): Resource<Food>
    suspend fun getFoodsByType(type: String): List<Food>
    suspend fun getFoodsByCategory(categoryId: String): List<Food>

    // Restaurant/Admin
    fun getMenuByRestaurantId(restaurantId: String): Flow<Resource<List<Food>>> // New
    suspend fun addFood(food: Food): Resource<Boolean>
    suspend fun updateFood(food: Food): Resource<Boolean>
    suspend fun deleteFood(id: String): Resource<Boolean>
    suspend fun updateFoodStatus(id: String, isAvailable: Boolean): Resource<Boolean>
    suspend fun searchFood(query: String): List<Food>
}