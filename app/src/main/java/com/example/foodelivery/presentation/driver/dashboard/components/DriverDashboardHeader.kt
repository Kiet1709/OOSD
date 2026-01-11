package com.example.foodelivery.presentation.driver.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodelivery.domain.model.User

@Composable
fun DriverDashboardHeader(
    driver: User?,
    isOnline: Boolean,
    onStatusChange: (Boolean) -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = driver?.avatarUrl?.ifBlank { "https://i.pravatar.cc/150" } ?: "https://i.pravatar.cc/150",
                    contentDescription = "Driver Avatar",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .clickable { menuExpanded = true },
                    contentScale = ContentScale.Crop
                )
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Hồ sơ") }, onClick = {
                        onProfileClick()
                        menuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Đăng xuất") }, onClick = {
                        onLogoutClick()
                        menuExpanded = false
                    })
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Xin chào,")
                    Text(
                        text = driver?.name ?: "Tài xế",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isOnline) "Online" else "Offline",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = isOnline,
                    onCheckedChange = onStatusChange
                )
            }
        }
    }
}