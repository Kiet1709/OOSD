package com.example.foodelivery.domain.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface IFoodRepository {
    // Client
    fun getMenu(): Flow<Resource<List<Food>>>
    suspend fun searchFood(query: String): List<Food>
    suspend fun getFoodsByType(type: String): List<Food>

    // Admin CRUD (Bổ sung đầy đủ)
    suspend fun getFoodDetail(id: String): Resource<Food>
    suspend fun addFood(food: Food): Resource<Boolean>
    suspend fun updateFood(food: Food): Resource<Boolean> // Mới
    suspend fun deleteFood(id: String): Resource<Boolean> // Mới
    suspend fun updateFoodStatus(id: String, isAvailable: Boolean): Resource<Boolean> // Mới
}