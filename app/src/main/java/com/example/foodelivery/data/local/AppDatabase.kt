package com.example.foodelivery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodelivery.data.local.dao.*
import com.example.foodelivery.data.local.entity.*

@Database(
    entities = [
        FoodEntity::class,
        CategoryEntity::class,
        UserEntity::class, // Bảng users sẽ được tạo lại
        CartEntity::class,
        OrderEntity::class,
        StoreEntity::class
    ],
    version = 4, // [FIX]: Tăng version để reset cấu trúc bảng
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun storeDao(): StoreDao
}