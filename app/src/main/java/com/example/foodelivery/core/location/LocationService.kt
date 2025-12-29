package com.example.foodelivery.core.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.foodelivery.R
import com.example.foodelivery.domain.repository.IDriverRepository // Import từ Domain
import com.example.foodelivery.core.location.LocationClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint // Bắt buộc để Hilt inject vào Service
class LocationService : Service() {

    @Inject
    lateinit var locationClient: LocationClient

    @Inject
    lateinit var driverRepository: IDriverRepository // Dùng để đẩy lên Firebase
    @Inject
    lateinit var auth: FirebaseAuth // Inject Auth để lấy UID tài xế

    // Scope riêng cho Service (SupervisorJob để lỗi con không làm chết Service)
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return START_STICKY // Tự khởi động lại nếu bị kill
    }

    private fun start() {
        createNotificationChannel()

        // 1. Tạo Notification để chạy Foreground
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Driver Mode")
            .setContentText("Đang cập nhật vị trí...")
            .setSmallIcon(R.mipmap.ic_launcher) // Thay icon app của bạn
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        // 2. Lắng nghe vị trí và đẩy lên Firebase
        val currentDriverId = auth.currentUser?.uid ?: return
        locationClient.getLocationUpdates(5000L) // 5s/lần
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude
                val lng = location.longitude
                val heading = location.bearing

                // Gọi Repository (Implementation sẽ đẩy lên Firestore/RealtimeDB)
                driverRepository.updateMyLocation(driverId = currentDriverId,
                    lat = location.latitude,
                    lng = location.longitude,
                    bearing = location.bearing)
            }
            .launchIn(serviceScope)
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Dọn dẹp Coroutine
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val CHANNEL_ID = "location_channel"
    }
}