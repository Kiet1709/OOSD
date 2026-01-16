package com.example.foodelivery.presentation.driver.delivery.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.foodelivery.ui.theme.PrimaryColor
import kotlin.math.atan2

@Composable
fun DriverMapSection(
    progress: Float, // 0f -> 1f (Tiến độ từ ViewModel)
    modifier: Modifier = Modifier
) {
    // Animation mượt mà cho tiến độ
    // Tăng duration lên một chút để vệt chạy nhìn mượt hơn nếu muốn (ví dụ 1000ms)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "mapAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE5E3DF)) // Màu nền bản đồ
    ) {

        // --- 1. VẼ ĐƯỜNG, TÒA NHÀ VÀ VỆT XE CHẠY TRÊN CANVAS ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // A. Vẽ tòa nhà giả lập (Nền)
            val blockColor = Color(0xFFDCDCDC)
            drawRect(color = blockColor, topLeft = Offset(0f, 0f), size = Size(width * 0.3f, height * 0.2f))
            drawRect(color = blockColor, topLeft = Offset(width * 0.4f, 0f), size = Size(width * 0.6f, height * 0.2f))
            drawRect(color = blockColor, topLeft = Offset(0f, height * 0.3f), size = Size(width * 0.3f, height * 0.4f))
            drawRect(color = blockColor, topLeft = Offset(width * 0.4f, height * 0.3f), size = Size(width * 0.4f, height * 0.2f))
            drawRect(color = blockColor, topLeft = Offset(width * 0.4f, height * 0.6f), size = Size(width * 0.6f, height * 0.4f))

            // B. Tạo con đường (Path) ĐẦY ĐỦ
            val roadPath = Path().apply {
                moveTo(width * 0.15f, height * 0.15f)
                lineTo(width * 0.7f, height * 0.15f)
                quadraticBezierTo(width * 0.9f, height * 0.15f, width * 0.9f, height * 0.25f)
                lineTo(width * 0.9f, height * 0.5f)
                lineTo(width * 0.5f, height * 0.5f)
                cubicTo(width * 0.2f, height * 0.5f, width * 0.2f, height * 0.8f, width * 0.5f, height * 0.8f)
                lineTo(width * 0.8f, height * 0.85f)
            }

            // C. Vẽ đường nền (Viền trắng + Lòng đường xám)
            drawPath(
                path = roadPath,
                color = Color.White,
                style = Stroke(width = 40.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            drawPath(
                path = roadPath,
                color = Color(0xFFF0F0F0),
                style = Stroke(width = 28.dp.toPx(), cap = StrokeCap.Round)
            )

            // --- D. [TÍNH NĂNG MỚI] VẼ VỆT ĐƯỜNG ĐÃ ĐI (TRAIL) ---
            // 1. Tạo PathMeasure để đo đường chính
            val pathMeasure = PathMeasure().apply { setPath(roadPath, false) }
            val totalLength = pathMeasure.length
            // 2. Tính khoảng cách đã đi được dựa trên progress
            val currentDistance = totalLength * animatedProgress

            // 3. Tạo một Path mới để chứa đoạn đường đã đi
            val traveledPath = Path()
            // 4. Cắt đoạn đường từ đầu (0f) đến vị trí hiện tại (currentDistance)
            pathMeasure.getSegment(0f, currentDistance, traveledPath, true)

            // 5. Vẽ đoạn đường đã đi với màu nổi bật (PrimaryColor)
            if (currentDistance > 0) {
                drawPath(
                    path = traveledPath,
                    color = PrimaryColor.copy(alpha = 0.8f), // Màu cam chủ đạo, hơi trong suốt nhẹ
                    style = Stroke(
                        width = 20.dp.toPx(), // Nhỏ hơn lòng đường một chút cho đẹp
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            // -------------------------------------------------------

            // E. Vẽ vạch kẻ đường nét đứt (Vẽ sau cùng để đè lên vệt màu)
            drawPath(
                path = roadPath,
                color = Color(0xFFCCCCCC),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                )
            )

            // F. Vẽ điểm đầu & cuối
            drawCircle(Color(0xFFE65100), radius = 8.dp.toPx(), center = Offset(width * 0.15f, height * 0.15f))
            drawCircle(PrimaryColor, radius = 8.dp.toPx(), center = Offset(width * 0.8f, height * 0.85f))
        }

        // --- 2. VẼ XE DI CHUYỂN (Phần này giữ nguyên) ---
        VehicleMarkerOverlay(progress = animatedProgress)
    }
}

@Composable
fun VehicleMarkerOverlay(progress: Float) {
    // ... (Giữ nguyên toàn bộ nội dung hàm VehicleMarkerOverlay như phiên bản trước) ...
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        val roadPath = remember(widthPx, heightPx) {
            Path().apply {
                moveTo(widthPx * 0.15f, heightPx * 0.15f)
                lineTo(widthPx * 0.7f, heightPx * 0.15f)
                quadraticBezierTo(widthPx * 0.9f, heightPx * 0.15f, widthPx * 0.9f, heightPx * 0.25f)
                lineTo(widthPx * 0.9f, heightPx * 0.5f)
                lineTo(widthPx * 0.5f, heightPx * 0.5f)
                cubicTo(widthPx * 0.2f, heightPx * 0.5f, widthPx * 0.2f, heightPx * 0.8f, widthPx * 0.5f, heightPx * 0.8f)
                lineTo(widthPx * 0.8f, heightPx * 0.85f)
            }
        }

        val pathMeasure = remember(roadPath) { PathMeasure().apply { setPath(roadPath, false) } }

        val distance = pathMeasure.length * progress

        val position = pathMeasure.getPosition(distance)
        val tangent = pathMeasure.getTangent(distance)

        val rotationAngle = (atan2(tangent.y, tangent.x) * (180 / Math.PI)).toFloat()

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (position.x - 25.dp.toPx()).toInt(),
                        y = (position.y - 25.dp.toPx()).toInt()
                    )
                }
                .size(50.dp)
                .rotate(rotationAngle)
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

        Icon(
            imageVector = Icons.Default.Store,
            contentDescription = "Store",
            tint = Color(0xFFE65100),
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (widthPx * 0.15f - 12.dp.toPx()).toInt(),
                        y = (heightPx * 0.15f - 24.dp.toPx()).toInt()
                    )
                }
                .size(24.dp)
        )

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Customer",
            tint = PrimaryColor,
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (widthPx * 0.8f - 12.dp.toPx()).toInt(),
                        y = (heightPx * 0.85f - 24.dp.toPx()).toInt()
                    )
                }
                .size(24.dp)
        )
    }
}