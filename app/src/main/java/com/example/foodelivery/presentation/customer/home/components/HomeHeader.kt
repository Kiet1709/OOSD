package com.example.foodelivery.presentation.customer.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List // [1] Icon đơn hàng
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
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
fun HomeHeader(
    user: User?,
    modifier: Modifier = Modifier,
    onCartClick: () -> Unit,
    onOrderClick: () -> Unit,
    onProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // --- AVATAR & INFO ---
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isMenuExpanded = true }
                    .padding(4.dp)
            ) {
                AsyncImage(
                    model = user?.avatarUrl?.ifBlank { "https://i.pravatar.cc/150?img=12" } ?: "https://i.pravatar.cc/150?img=12",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Xin chào,",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = user?.name ?: "Khách hàng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- MENU ---
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                // [3] THÊM MỤC ĐƠN HÀNG VÀO ĐÂY
                DropdownMenuItem(
                    text = { Text("Đơn hàng của tôi") },
                    onClick = { isMenuExpanded = false; onOrderClick() },
                    leadingIcon = { Icon(Icons.Default.List, contentDescription = null) }
                )

                DropdownMenuItem(
                    text = { Text("Hồ sơ cá nhân") },
                    onClick = { isMenuExpanded = false; onProfileClick() },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Đổi mật khẩu") },
                    onClick = { isMenuExpanded = false; onChangePasswordClick() },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                )

                DropdownMenuItem(
                    text = { Text("Cài đặt") },
                    onClick = { isMenuExpanded = false; onSettingsClick() },
                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Đăng xuất", color = Color.Red, fontWeight = FontWeight.Bold) },
                    onClick = { isMenuExpanded = false; onLogoutClick() },
                    leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) }
                )
            }
        }

        // --- GIỎ HÀNG ---
        IconButton(
            onClick = onCartClick,
            modifier = Modifier
                .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                .size(45.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                tint = PrimaryColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}