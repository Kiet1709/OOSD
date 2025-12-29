package com.example.foodelivery.domain.usecase.customer

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMenuUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    operator fun invoke(): Flow<Resource<List<Food>>> {
        return repository.getMenu()
    }
}