package com.example.foodelivery.di

import com.example.foodelivery.data.repository.CategoryRepositoryImpl
import com.example.foodelivery.domain.repository.ICategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    @Provides
    @Singleton
    fun provideCategoryRepository(): ICategoryRepository {
        return CategoryRepositoryImpl()
    }
}