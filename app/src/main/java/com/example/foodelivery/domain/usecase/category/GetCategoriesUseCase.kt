package com.example.foodelivery.domain.usecase.category

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Category
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.example.foodelivery.presentation.admin.category.list.contract.CategoryUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// 1. Get Categories (Search + Map to UI Model)
class GetCategoriesUseCase @Inject constructor(private val repo: ICategoryRepository) {
    operator fun invoke(query: String): Flow<Resource<List<CategoryUiModel>>> {
        return repo.getCategories().map { result ->
            when (result) {
                is Resource.Success -> {
                    val data = result.data ?: emptyList()
                    val filtered = if (query.isBlank()) data else data.filter {
                        it.name.contains(query, ignoreCase = true)
                    }
                    // Map Domain -> UI Model
                    val uiModels = filtered.map {
                        CategoryUiModel(it.id, it.name, it.imageUrl, totalFoods = 0)
                    }
                    Resource.Success(uiModels)
                }
                is Resource.Error -> Resource.Error(result.message ?: "Lỗi tải dữ liệu")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }
}

// 2. Get Single Category (For Edit)
class GetCategoryByIdUseCase @Inject constructor(private val repo: ICategoryRepository) {
    suspend operator fun invoke(id: String) = repo.getCategoryById(id)
}

// 3. Add
class AddCategoryUseCase @Inject constructor(private val repo: ICategoryRepository) {
    suspend operator fun invoke(name: String, imageUrl: String): Resource<Boolean> {
        return repo.addCategory(Category(id = "", name = name, imageUrl = imageUrl))
    }
}

// 4. Update
class UpdateCategoryUseCase @Inject constructor(private val repo: ICategoryRepository) {
    suspend operator fun invoke(category: Category): Resource<Boolean> {
        return repo.updateCategory(category)
    }
}

// 5. Delete
class DeleteCategoryUseCase @Inject constructor(private val repo: ICategoryRepository) {
    suspend operator fun invoke(id: String) = repo.deleteCategory(id)
}