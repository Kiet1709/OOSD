package com.example.foodelivery.domain.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.StoreInfo
import kotlinx.coroutines.flow.Flow

interface IStoreRepository {
    fun getStoreInfo(): Flow<Resource<StoreInfo>>
    suspend fun updateStoreInfo(info: StoreInfo): Resource<Boolean>
}