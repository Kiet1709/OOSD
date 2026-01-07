package com.example.foodelivery.domain.usecase.food

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import javax.inject.Inject

class AddFoodUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(food: Food): Resource<Boolean> {
        // Business Rule: Validate tên món không được trống
        if (food.name.isBlank()) {
            return Resource.Error("Tên món ăn không được để trống")
        }
        if (food.price < 0) {
            return Resource.Error("Giá tiền không hợp lệ")
        }
        
        return repository.addFood(food)
    }
}