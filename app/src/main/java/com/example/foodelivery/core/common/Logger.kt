package com.example.foodelivery.core.common

import android.util.Log
// import com.example.fooddelivery.BuildConfig // Un-comment khi đã build app

object Logger {
    private const val TAG = "FoodAppLogger"

    fun d(msg: String) {
        // if (BuildConfig.DEBUG)
        Log.d(TAG, msg)
    }

    fun e(msg: String, tr: Throwable? = null) {
        // if (BuildConfig.DEBUG)
        Log.e(TAG, msg, tr)
    }
}