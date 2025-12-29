package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Category
import com.example.foodelivery.domain.repository.ICategoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor() : ICategoryRepository {

    // Mock Data
    private val mockList = mutableListOf(
        Category("1", "Đồ ăn nhanh", "https://via.placeholder.com/150"),
        Category("2", "Đồ uống", "https://via.placeholder.com/150"),
        Category("3", "Tráng miệng", "https://via.placeholder.com/150")
    )

    override fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        delay(800)
        emit(Resource.Success(mockList.toList()))
    }

    override suspend fun getCategoryById(id: String): Resource<Category> {
        delay(500)
        val item = mockList.find { it.id == id }
        return if (item != null) Resource.Success(item) else Resource.Error("Không tìm thấy")
    }

    override suspend fun addCategory(category: Category): Resource<Boolean> {
        delay(1000)
        mockList.add(category.copy(id = (mockList.size + 1).toString()))
        return Resource.Success(true)
    }

    override suspend fun updateCategory(category: Category): Resource<Boolean> {
        delay(1000)
        val index = mockList.indexOfFirst { it.id == category.id }
        return if (index != -1) {
            mockList[index] = category
            Resource.Success(true)
        } else Resource.Error("Lỗi cập nhật")
    }

    override suspend fun deleteCategory(id: String): Resource<Boolean> {
        delay(800)
        val removed = mockList.removeIf { it.id == id }
        return if (removed) Resource.Success(true) else Resource.Error("Xóa thất bại")
    }
}