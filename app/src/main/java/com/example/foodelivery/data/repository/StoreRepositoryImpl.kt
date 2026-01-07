package com.example.foodelivery.data.repository

import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.data.local.dao.StoreDao
import com.example.foodelivery.data.local.entity.StoreEntity
import com.example.foodelivery.data.local.entity.toDomain
import com.example.foodelivery.data.local.entity.toEntity
import com.example.foodelivery.domain.model.StoreInfo
import com.example.foodelivery.domain.repository.IStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreRepositoryImpl @Inject constructor(
    private val dao: StoreDao
) : IStoreRepository {

    private val defaultStore = StoreEntity(
        name = "Nhà Hàng Ngon Tuyệt",
        address = "123 Đường ABC, Quận 1, TP.HCM",
        phoneNumber = "0909123456",
        description = "Chuyên phục vụ các món ăn ngon, hợp vệ sinh, giao hàng nhanh chóng.",
        avatarUrl = "https://cdn-icons-png.flaticon.com/512/4039/4039232.png",
        coverUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5"
    )

    override fun getStoreInfo(): Flow<Resource<StoreInfo>> {
        return dao.getStoreInfo().map { entity ->
            if (entity == null) {
                // Nếu chưa có, trả về mặc định (nhưng chưa lưu vào DB để user tự sửa)
                Resource.Success(defaultStore.toDomain())
            } else {
                Resource.Success(entity.toDomain())
            }
        }
    }

    override suspend fun updateStoreInfo(info: StoreInfo): Resource<Boolean> {
        return try {
            dao.saveStoreInfo(info.toEntity())
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Lỗi lưu thông tin")
        }
    }
}