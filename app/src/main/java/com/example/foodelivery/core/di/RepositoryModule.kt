package com.example.foodelivery.core.di

import com.example.foodelivery.data.local.dao.UserDao
import com.example.foodelivery.data.repository.*
import com.example.foodelivery.domain.repository.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFoodRepository(impl: FoodRepositoryImpl): IFoodRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): ICartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): IOrderRepository

    @Binds
    @Singleton
    abstract fun bindDriverRepository(impl: DriverRepositoryImpl): IDriverRepository

    companion object {
        @Provides
        @Singleton
        fun provideUserRepository(firestore: FirebaseFirestore, storage: FirebaseStorage, userDao: UserDao): IUserRepository {
            return UserRepositoryImpl(firestore, storage, userDao)
        }
    }
}