package com.example.foodelivery.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.example.foodelivery.core.location.LocationClient
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DefaultLocationClient @Inject constructor(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission") // Permission đã được check ở UI/Service trước khi gọi
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            // 1. Kiểm tra quyền (Double check)
            if (!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Thiếu quyền truy cập vị trí")
            }

            // 2. Kiểm tra GPS/Network
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS chưa được bật")
            }

            // 3. Cấu hình Request (High Accuracy cho Driver)
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
                .setMinUpdateIntervalMillis(interval)
                .setWaitForAccurateLocation(false)
                .build()

            // 4. Callback nhận vị trí
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(location) } // Đẩy vào Flow
                    }
                }
            }

            // 5. Đăng ký updates
            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            // 6. Cleanup khi Flow bị hủy (Rất quan trọng để tránh Memory Leak)
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    // Extension check quyền nội bộ
    private fun Context.hasLocationPermission(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}