package com.example.foodelivery.core.location

import android.location.Location

object MapUtils {
    // Tính khoảng cách (mét)
    fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }

    // Tính thời gian dự kiến (phút) với vận tốc trung bình (km/h)
    fun calculateETA(distanceInMeters: Float, averageSpeedKmH: Double = 30.0): Int {
        if (averageSpeedKmH <= 0) return 0
        val speedMetersPerMin = (averageSpeedKmH * 1000) / 60
        return (distanceInMeters / speedMetersPerMin).toInt()
    }

    // Format hiển thị đẹp
    fun formatDistance(meters: Float): String {
        return if (meters < 1000) {
            "${meters.toInt()} m"
        } else {
            String.format("%.1f km", meters / 1000)
        }
    }
}