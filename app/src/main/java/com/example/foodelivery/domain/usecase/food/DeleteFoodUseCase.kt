package com.example.foodelivery.domain.usecase.food

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.repository.IFoodRepository
import javax.inject.Inject

class DeleteFoodUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    suspend operator fun invoke(id: String): Resource<Boolean> {
        return repository.deleteFood(id)
    }
}