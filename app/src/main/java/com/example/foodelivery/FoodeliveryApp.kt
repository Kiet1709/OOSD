package com.example.foodelivery

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodDeliveryApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}