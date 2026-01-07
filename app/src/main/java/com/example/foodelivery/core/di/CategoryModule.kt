package com.example.foodelivery.core.di

import com.example.foodelivery.data.local.AppDatabase
import com.example.foodelivery.data.repository.CategoryRepositoryImpl
import com.example.foodelivery.domain.repository.ICategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideCategoryRepository(
        db: AppDatabase,
        firestore: FirebaseFirestore
    ): ICategoryRepository {
        return CategoryRepositoryImpl(db, firestore)
    }
}