package com.example.foodelivery.domain.usecase.food

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// 1. Lấy danh sách (Dùng chung cho cả Admin list)
class GetFoodsUseCase @Inject constructor(private val repo: IFoodRepository) {
    operator fun invoke(): Flow<Resource<List<Food>>> = repo.getMenu()
}

// 2. Lấy chi tiết
class GetFoodDetailUseCase @Inject constructor(private val repo: IFoodRepository) {
    suspend operator fun invoke(id: String) = repo.getFoodDetail(id)
}

// 3. Thêm món
class AddFoodUseCase @Inject constructor(private val repo: IFoodRepository) {
    suspend operator fun invoke(food: Food) = repo.addFood(food)
}

// 4. Cập nhật món
class UpdateFoodUseCase @Inject constructor(private val repo: IFoodRepository) {
    suspend operator fun invoke(food: Food) = repo.updateFood(food)
}

// 5. Xóa món
class DeleteFoodUseCase @Inject constructor(private val repo: IFoodRepository) {
    suspend operator fun invoke(id: String) = repo.deleteFood(id)
}

// 6. Đổi trạng thái (Còn hàng / Hết hàng)
class ToggleFoodStatusUseCase @Inject constructor(private val repo: IFoodRepository) {
    suspend operator fun invoke(id: String, isAvailable: Boolean) = repo.updateFoodStatus(id, isAvailable)
}