package com.example.foodelivery.domain.usecase.admin

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.StoreInfo
import com.example.foodelivery.domain.repository.IStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStoreInfoUseCase @Inject constructor(
    private val repository: IStoreRepository
) {
    operator fun invoke(): Flow<Resource<StoreInfo>> {
        return repository.getStoreInfo()
    }
}