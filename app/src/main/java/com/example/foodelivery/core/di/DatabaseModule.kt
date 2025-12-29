package com.example.foodelivery.core.di

import android.content.Context
import androidx.room.Room
import com.example.foodelivery.core.common.Constants
import com.example.foodelivery.data.local.FoodDatabase
import com.example.foodelivery.data.local.dao.CartDao
import com.example.foodelivery.data.local.dao.CategoryDao
import com.example.foodelivery.data.local.dao.FoodDao
import com.example.foodelivery.data.local.dao.OrderDao
import com.example.foodelivery.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 1. KHỞI TẠO DATABASE (SINGLETON)
     * - Load file từ assets/database/food_delivery.db
     * - Nếu cấu trúc thay đổi -> Xóa cũ tạo mới (fallbackToDestructiveMigration)
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FoodDatabase {
        return Room.databaseBuilder(
            context,
            FoodDatabase::class.java,
            Constants.DB_NAME
        )
//            .createFromAsset("databases/fooddelivery.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * 2. CUNG CẤP CÁC DAO (DATA ACCESS OBJECT)
     * Giúp Repository chỉ cần Inject DAO, không cần quan tâm đến Database
     */

    @Provides
    fun provideFoodDao(db: FoodDatabase): FoodDao {
        return db.foodDao()
    }

    @Provides
    fun provideCategoryDao(db: FoodDatabase): CategoryDao {
        return db.categoryDao()
    }

    @Provides
    fun provideUserDao(db: FoodDatabase): UserDao {
        return db.userDao()
    }

    @Provides
    fun provideCartDao(db: FoodDatabase): CartDao {
        return db.cartDao()
    }

    @Provides
    fun provideOrderDao(db: FoodDatabase): OrderDao {
        return db.orderDao()
    }
}