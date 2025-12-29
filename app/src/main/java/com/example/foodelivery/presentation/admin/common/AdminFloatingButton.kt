package com.example.foodelivery.presentation.admin.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AdminFloatingButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary, // Màu chủ đạo App
        contentColor = Color.White
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm mới")
    }
}