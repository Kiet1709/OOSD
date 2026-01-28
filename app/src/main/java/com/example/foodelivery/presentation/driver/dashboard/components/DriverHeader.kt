package com.example.foodelivery.presentation.driver.dashboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.ui.theme.PrimaryColor

@Composable
fun DriverHeader(
    user: User?,
    isOnline: Boolean,
    onToggleStatus: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onRevenueClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val statusColor by animateColorAsState(if (isOnline) Color(0xFF4CAF50) else Color(0xFFE53935), label = "color")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // INFO SECTION
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { isMenuExpanded = true }
                    .padding(4.dp)
            ) {
                Box {
                    AsyncImage(
                        model = user?.avatarUrl?.ifBlank { "https://i.pravatar.cc/150?img=driver" } ?: "https://i.pravatar.cc/150?img=driver",
                        contentDescription = "Avatar",
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Box(Modifier.size(14.dp).background(statusColor, CircleShape).border(2.dp, Color.White, CircleShape).align(Alignment.BottomEnd))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(if(isOnline) "Đang hoạt động" else "Đang nghỉ", style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold)
                    Text(user?.name ?: "Bác tài", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
            // MENU
            DropdownMenu(expanded = isMenuExpanded, onDismissRequest = { isMenuExpanded = false }, modifier = Modifier.background(Color.White)) {
                DropdownMenuItem(
                    text = { Text("Hồ sơ tài xế") },
                    onClick = { isMenuExpanded = false; onProfileClick() },
                    leadingIcon = { Icon(Icons.Default.Person, null) }
                )
                DropdownMenuItem(
                    text = { Text("Đổi mật khẩu") },
                    onClick = { isMenuExpanded = false; onChangePasswordClick() },
                    leadingIcon = { Icon(Icons.Default.Lock, null) }
                )
                DropdownMenuItem(text = { Text("Doanh thu") }, onClick = { isMenuExpanded = false; onRevenueClick() }, leadingIcon = { Icon(Icons.Default.AttachMoney, null) })
                HorizontalDivider()
                DropdownMenuItem(text = { Text("Đăng xuất", color = Color.Red) }, onClick = { isMenuExpanded = false; onLogoutClick() }, leadingIcon = { Icon(Icons.Default.ExitToApp, null, tint = Color.Red) })
            }
        }

        // SWITCH BUTTON
        Button(
            onClick = onToggleStatus,
            colors = ButtonDefaults.buttonColors(containerColor = statusColor.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.height(36.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Icon(Icons.Default.PowerSettingsNew, null, tint = statusColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(if (isOnline) "ONLINE" else "OFFLINE", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = statusColor)
        }
    }
}