package com.example.foodelivery.presentation.admin.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
@Composable
fun DashboardHeader(
    // 1. Dữ liệu hiển thị (Input Data)
    adminName: String,
    avatarUrl: String?,
    hasUnreadNotifications: Boolean = false, // Senior: Thêm cờ check có tin mới không

    // 2. Các hành động (Events/Callbacks)
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp), // Padding chuẩn Grid 8dp
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- PHẦN 1: TEXT CHÀO MỪNG ---
        Column(verticalArrangement = Arrangement.Center) {
            Text(
                text = "Quản trị viên",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = adminName,
                style = MaterialTheme.typography.titleLarge, // To hơn, rõ hơn
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // --- PHẦN 2: ACTIONS (CHUÔNG + AVATAR) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // A. Nút Thông Báo (Có Badge chấm đỏ)
            NotificationIconBtn(
                hasUnread = hasUnreadNotifications,
                onClick = onNotificationClick
            )

            // B. Avatar & Dropdown Menu
            AvatarWithMenu(
                name = adminName,
                avatarUrl = avatarUrl,
                onProfileClick = onProfileClick,
                onChangePasswordClick = onChangePasswordClick,
                onSettingsClick = onSettingsClick,
                onHelpClick = onHelpClick,
                onLogoutClick = onLogoutClick
            )
        }
    }
}

// --- SUB-COMPONENT 1: Nút chuông thông báo ---
// Tách ra để code chính gọn gàng
@Composable
private fun NotificationIconBtn(
    hasUnread: Boolean,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.TopEnd) {
        IconButton(
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Thông báo")
        }

        // Senior: Hiển thị chấm đỏ nếu có tin mới
        if (hasUnread) {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp) // Căn chỉnh vị trí chấm đỏ
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}

// --- SUB-COMPONENT 2: Avatar & Menu Dropdown ---
// --- SUB-COMPONENT 2: Avatar & Menu Dropdown ---
@Composable
private fun AvatarWithMenu(
    name: String,
    avatarUrl: String?,
    onProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    // Box này đóng vai trò là "mỏ neo" (anchor point) cho DropdownMenu
    Box {
        var expanded by remember { mutableStateOf(false) }

        // 1. Ảnh Avatar (Nút kích hoạt menu)
        AsyncImage(
            model = avatarUrl ?: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTH8eKkdv_3Y3GdKEVtfR-WPmRNGFasdtzvLg&s",
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .clickable { expanded = true } // Click để mở menu
        )
        // 2. Menu thả xuống
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 0.dp, y = 8.dp),
            modifier = Modifier

        ) {
            // --- Header của Menu ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Đang đăng nhập",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // Tránh tràn text tên dài
                )
            }

            HorizontalDivider()

            // --- Các Options ---

            DropdownMenuItem(
                text = { Text("Đổi mật khẩu") },
                onClick = { expanded = false; onChangePasswordClick() },
                leadingIcon = { Icon(Icons.Outlined.LockReset, null) }
            )
            DropdownMenuItem(
                text = { Text("Cài đặt") },
                onClick = { expanded = false; onSettingsClick() },
                leadingIcon = { Icon(Icons.Outlined.Settings, null) }
            )
            DropdownMenuItem(
                text = { Text("Trợ giúp") },
                onClick = { expanded = false; onHelpClick() },
                leadingIcon = { Icon(Icons.Outlined.HelpOutline, null) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // --- Logout ---
            DropdownMenuItem(
                text = { Text("Đăng xuất", color = MaterialTheme.colorScheme.error) },
                onClick = { expanded = false; onLogoutClick() },
                leadingIcon = {
                    Icon(Icons.Outlined.Logout, null, tint = MaterialTheme.colorScheme.error)
                },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.error,
                    leadingIconColor = MaterialTheme.colorScheme.error
                )
            )
        }
    }
}