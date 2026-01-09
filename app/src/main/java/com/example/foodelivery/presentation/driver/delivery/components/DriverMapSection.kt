package com.example.foodelivery.presentation.driver.delivery.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.foodelivery.ui.theme.PrimaryColor

@Composable
fun DriverMapSection(
    progress: Float, // 0f -> 1f (Tiến độ di chuyển)
    modifier: Modifier = Modifier
) {
    // Demo Map: Một nền xám với Icon xe di chuyển chéo màn hình
    Box(
        modifier = modifier.background(Color(0xFFE0E0E0))
    ) {
        Text(
            text = "Google Maps View (Simulator)",
            modifier = Modifier.align(Alignment.Center),
            color = Color.Gray
        )

        // Animation di chuyển xe
        val animatedProgress by animateFloatAsState(targetValue = progress, label = "mapAnimation")



        // Giả lập xe chạy từ góc trái trên xuống góc phải dưới
        // Bạn có thể thay đổi logic này khi tích hợp Map thật
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = (300 * animatedProgress).dp,
                    y = (600 * animatedProgress).dp
                )
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape)
                    .background(PrimaryColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBike,
                    contentDescription = "Driver",
                    tint = PrimaryColor,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}