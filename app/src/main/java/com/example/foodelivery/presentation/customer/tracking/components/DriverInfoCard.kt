package com.example.foodelivery.presentation.customer.tracking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.customer.tracking.contract.DriverUiModel
import com.example.foodelivery.ui.theme.PrimaryColor

@Composable
fun DriverInfoCard(
    driver: DriverUiModel,
    onCall: () -> Unit,
    onMessage: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = driver.avatarUrl ?: "https://via.placeholder.com/150",
            contentDescription = "Driver Avatar",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(driver.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                Text(" ${driver.rating} • ${driver.licensePlate}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        // Action Buttons
        IconButton(
            onClick = onMessage,
            modifier = Modifier.background(Color(0xFFF0F0F0), CircleShape)
        ) {
            Icon(Icons.Default.Message, contentDescription = "Chat", tint = PrimaryColor)
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onCall,
            modifier = Modifier.background(PrimaryColor, CircleShape)
        ) {
            Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White)
        }
    }
}