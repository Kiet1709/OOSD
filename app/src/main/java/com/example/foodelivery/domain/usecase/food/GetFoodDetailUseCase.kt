package com.example.foodelivery.domain.usecase.food

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import javax.inject.Inject

class GetFoodDetailUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(id: String): Resource<Food> {
        return repository.getFoodDetail(id)
    }
}