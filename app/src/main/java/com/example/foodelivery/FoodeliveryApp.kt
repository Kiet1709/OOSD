package com.example.foodelivery

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodDeliveryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Nơi khởi tạo các thư viện bên thứ 3 (nếu cần)
    }
}