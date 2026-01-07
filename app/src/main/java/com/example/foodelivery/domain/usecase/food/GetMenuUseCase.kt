package com.example.foodelivery.domain.usecase.food

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.repository.IFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Design Pattern: Use Case / Interactor
 * Chức năng: Lấy danh sách thực đơn.
 * Có thể thêm logic filter, sort, business rule ở đây trước khi trả về ViewModel.
 */
class GetMenuUseCase @Inject constructor(
    private val repository: IFoodRepository
) {
    operator fun invoke(): Flow<Resource<List<Food>>> {
        return repository.getMenu()
    }
}