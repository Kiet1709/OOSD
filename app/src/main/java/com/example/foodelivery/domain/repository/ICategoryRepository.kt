package com.example.foodelivery.domain.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
    fun getCategories(): Flow<Resource<List<Category>>>
    suspend fun getCategoryById(id: String): Resource<Category> // MỚI
    suspend fun addCategory(category: Category): Resource<Boolean> // MỚI
    suspend fun updateCategory(category: Category): Resource<Boolean> // MỚI
    suspend fun deleteCategory(id: String): Resource<Boolean>
}