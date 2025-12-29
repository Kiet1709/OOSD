package com.example.foodelivery.presentation.driver.delivery.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.foodelivery.ui.theme.PrimaryColor

@Composable
fun DriverMapSection(
    progress: Float, // 0f -> 1f (Tiến độ xe chạy)
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize().background(Color(0xFFE3F2FD))) {
        // Animation mượt mà cho xe
        val animatedProgress by animateFloatAsState(targetValue = progress, label = "DriverMove")

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Giả lập tọa độ: Điểm A -> Điểm B
            val start = Offset(w * 0.2f, h * 0.8f)
            val end = Offset(w * 0.8f, h * 0.3f)

            // 1. Vẽ đường đi (Nét đứt)
            drawLine(
                color = Color.Gray,
                start = start,
                end = end,
                strokeWidth = 6.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 20f))
            )

            // 2. Vẽ Điểm xuất phát
            drawCircle(color = Color.DarkGray, radius = 20f, center = start)

            // 3. Vẽ Điểm đích (Nhà hàng hoặc Khách)
            drawCircle(color = PrimaryColor, radius = 30f, center = end)

            // 4. Vẽ Xe Tài Xế (Di chuyển)
            val currentX = start.x + (end.x - start.x) * animatedProgress
            val currentY = start.y + (end.y - start.y) * animatedProgress

            drawCircle(color = Color.Black, radius = 25f, center = Offset(currentX, currentY))
            // Đèn xe
            drawCircle(color = Color.Yellow, radius = 10f, center = Offset(currentX, currentY))
        }

        Box(Modifier.align(Alignment.Center)) {
            Text("Live Navigation Map", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}