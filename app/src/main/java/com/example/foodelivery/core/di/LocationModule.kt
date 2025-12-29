package com.example.foodelivery.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import android.content.Context
import com.example.foodelivery.core.location.DefaultLocationClient
import com.example.foodelivery.core.location.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    // 1. Cung cấp FusedLocationProviderClient (Thư viện Google Map)
    companion object {
        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }
    }


    // 2. Bind Interface LocationClient vào DefaultLocationClient
    @Binds
    @Singleton
    abstract fun bindLocationClient(
        impl: DefaultLocationClient
    ): LocationClient
}