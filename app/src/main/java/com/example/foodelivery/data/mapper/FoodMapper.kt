package com.example.foodelivery.data.mapper

import com.example.foodelivery.data.local.entity.FoodEntity
import com.example.foodelivery.data.remote.dto.FoodDto
import com.example.foodelivery.domain.model.Food

// ==============================================================================
// 1. ENTITY (Local) -> DOMAIN (UI)
// Dùng khi: Hiển thị Menu từ Cache (Offline First)
// ==============================================================================
fun FoodEntity.toDomain(): Food = Food(
    id = id,
    name = name,
    description = description ?: "",
    price = price,
    imageUrl = imageUrl ?: "",
    categoryId = categoryId,
    rating = rating,
    isAvailable = isAvailable
)

// ==============================================================================
// 2. DTO (Remote) -> ENTITY (Cache)
// Dùng khi: Fetch dữ liệu từ Firebase về lưu vào SQLite
// ==============================================================================
fun FoodDto.toEntity(): FoodEntity = FoodEntity(
    id = id,
    name = name ?: "Unknown Food",
    description = description ?: "",
    price = price ?: 0.0,
    imageUrl = imageUrl ?: "",
    categoryId = categoryId ?: "",
    rating = rating ?: 0.0,
    isAvailable = isAvailable ?: true
)

// ==============================================================================
// 3. DOMAIN (UI) -> DTO (Remote)
// Dùng khi: Admin tạo/sửa món ăn gửi lên Server
// ==============================================================================
fun Food.toDto(): FoodDto = FoodDto(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    rating = rating,
    isAvailable = isAvailable
)

// ==============================================================================
// 4. DOMAIN (UI) -> ENTITY (Local)
// Dùng khi: Optimistic UI (Lưu vào máy ngay lập tức khi user sửa, không chờ mạng)
// ==============================================================================
fun Food.toEntity(): FoodEntity = FoodEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    rating = rating,
    isAvailable = isAvailable
)

// ==============================================================================
// 5. ENTITY (Local) -> DTO (Remote)
// Dùng khi: Background Sync (Worker quét DB local đẩy lên Server)
// ==============================================================================
fun FoodEntity.toDto(): FoodDto = FoodDto(
    id = id,
    name = name,
    description = description,
    price = price,
    imageUrl = imageUrl,
    categoryId = categoryId,
    rating = rating,
    isAvailable = isAvailable
)

// ==============================================================================
// 6. DTO (Remote) -> DOMAIN (UI)
// Dùng khi: Search Online (Tìm kiếm trực tiếp từ API, hiển thị luôn không lưu DB)
// ==============================================================================
fun FoodDto.toDomain(): Food = Food(
    id = id,
    name = name ?: "Unknown",
    description = description ?: "",
    price = price ?: 0.0,
    imageUrl = imageUrl ?: "",
    categoryId = categoryId ?: "",
    rating = rating ?: 0.0,
    isAvailable = isAvailable ?: true
)