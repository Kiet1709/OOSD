package com.example.foodelivery.presentation.admin.food.detail.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun FoodImagePicker(
    selectedUri: Uri?,
    serverUrl: String?,
    onImageSelected: (Uri?) -> Unit
) {
    // Launcher để mở thư viện ảnh
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onImageSelected(uri) }
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable {
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Logic hiển thị:
        // 1. Nếu có Uri (vừa chọn) -> Hiển thị Uri
        // 2. Nếu không có Uri, nhưng có Url (ảnh cũ) -> Hiển thị Url
        // 3. Nếu không có cả 2 -> Hiển thị Placeholder (Icon thêm ảnh)

        if (selectedUri != null) {
            AsyncImage(
                model = selectedUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!serverUrl.isNullOrEmpty()) {
            AsyncImage(
                model = serverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder: Viền nét đứt (Dashed Border)
            EmptyPlaceholder()
        }
    }
}

@Composable
private fun EmptyPlaceholder() {
    val stroke = Stroke(
        width = 4f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
    )
    val color = MaterialTheme.colorScheme.outline

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }
    ) {
        Icon(
            imageVector = Icons.Default.AddPhotoAlternate,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Thêm hình ảnh", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}