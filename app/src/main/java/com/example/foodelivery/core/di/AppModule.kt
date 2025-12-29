package com.example.foodelivery.core.di

import com.example.foodelivery.core.common.DispatcherProvider
import com.example.foodelivery.core.common.StandardDispatchers
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider {
        return StandardDispatchers()
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context



}