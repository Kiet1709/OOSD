package com.example.foodelivery.core.di

import android.content.Context
import androidx.room.Room
import com.example.foodelivery.data.local.AppDatabase
import com.example.foodelivery.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "food_delivery_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides @Singleton fun provideFoodDao(db: AppDatabase) = db.foodDao()
    @Provides @Singleton fun provideCategoryDao(db: AppDatabase) = db.categoryDao()
    @Provides @Singleton fun provideUserDao(db: AppDatabase) = db.userDao()
    @Provides @Singleton fun provideCartDao(db: AppDatabase) = db.cartDao()
    @Provides @Singleton fun provideOrderDao(db: AppDatabase) = db.orderDao()
    @Provides @Singleton fun provideStoreDao(db: AppDatabase) = db.storeDao() // Thêm provider
}