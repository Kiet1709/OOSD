package com.example.foodelivery.domain.usecase.food

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Chỉ giữ lại GetFoodsUseCase và ToggleFoodStatusUseCase chưa được tách
// Các UseCase khác (Add, Update, Delete, Detail) đã có file riêng, cần xóa khỏi đây để tránh Redeclaration

class GetFoodsUseCase @Inject constructor(private val repo: IFoodRepository) {
    operator fun invoke(): Flow<Resource<List<Food>>> = repo.getMenu()
}

class ToggleFoodStatusUseCase @Inject constructor(private val repo: IFoodRepository) {
    suspend operator fun invoke(id: String, isAvailable: Boolean) = repo.updateFoodStatus(id, isAvailable)
}