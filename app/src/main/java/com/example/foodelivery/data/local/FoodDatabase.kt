package com.example.foodelivery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodelivery.data.local.dao.*
import com.example.foodelivery.data.local.entity.*

@Database(
    entities = [
        FoodEntity::class,
        CategoryEntity::class,
        UserEntity::class,
        CartEntity::class,
        OrderEntity::class
    ],
    version = 2, // Nếu trước đó là 1 thì tăng lên 2, hoặc gỡ app cài lại
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
}