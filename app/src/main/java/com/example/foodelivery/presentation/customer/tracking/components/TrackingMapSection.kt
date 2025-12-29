package com.example.foodelivery.presentation.customer.tracking.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.example.foodelivery.ui.theme.PrimaryColor // Import màu chủ đạo

@Composable
fun TrackingMapSection(
    progress: Float, // 0.0 -> 1.0
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Color(0xFFE0F7FA))) {
        // Animation mượt mà cho xe chạy
        val animatedProgress by animateFloatAsState(targetValue = progress, label = "CarAnim")

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Tọa độ giả lập (Đường chéo)
            val start = Offset(w * 0.2f, h * 0.7f) // Điểm xuất phát
            val end = Offset(w * 0.8f, h * 0.3f)   // Điểm đích

            // 1. Vẽ đường đi (Nét đứt)
            drawLine(
                color = Color.Gray,
                start = start,
                end = end,
                strokeWidth = 6.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f))
            )

            // 2. Vẽ các điểm mốc
            drawCircle(color = Color.Red, radius = 20f, center = start) // Shop
            drawCircle(color = PrimaryColor, radius = 25f, center = end)   // Home

            // 3. Vẽ xe (Di chuyển theo progress)
            val currentX = start.x + (end.x - start.x) * animatedProgress
            val currentY = start.y + (end.y - start.y) * animatedProgress

            drawCircle(color = Color.Black, radius = 35f, center = Offset(currentX, currentY))
            // Giả làm cái đèn xe
            drawCircle(color = Color.Yellow, radius = 10f, center = Offset(currentX, currentY))
        }

        // Label
        Box(Modifier.align(Alignment.Center)) {
            Text("Live Tracking Map", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
        }
    }
}