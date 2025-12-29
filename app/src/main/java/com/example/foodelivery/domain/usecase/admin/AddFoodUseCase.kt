package com.example.foodelivery.domain.usecase.admin

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import javax.inject.Inject

class AddFoodUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(food: Food): Resource<Boolean> {
        if (food.price <= 0) return Resource.Error("Giá món ăn không hợp lệ")
        if (food.name.isBlank()) return Resource.Error("Tên món ăn không được để trống")

        return repository.addFood(food)
    }
}