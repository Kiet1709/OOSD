package com.example.foodelivery.presentation.customer.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import com.example.foodelivery.ui.theme.PrimaryColor

@Composable
fun HomeHeader(
    userName: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
    // [CÁC SỰ KIỆN CLICK]
    onCartClick: () -> Unit,      // Click giỏ hàng
    onProfileClick: () -> Unit,   // Click menu: Hồ sơ
    onSettingsClick: () -> Unit,  // Click menu: Cài đặt
    onLogoutClick: () -> Unit     // Click menu: Đăng xuất
) {
    // 1. Biến trạng thái để Bật/Tắt Menu
    var isMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // --- PHẦN TRÁI: AVATAR & LỜI CHÀO (BỌC TRONG BOX ĐỂ GẮN MENU) ---
        Box {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isMenuExpanded = true } // <--- Bấm vào đây để mở Menu
                    .padding(4.dp)
            ) {
                AsyncImage(
                    model = avatarUrl ?: "https://i.pravatar.cc/150?img=12",
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
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- MENU XỔ XUỐNG (DROPDOWN) ---
            DropdownMenu(
                expanded = isMenuExpanded,
                onDismissRequest = { isMenuExpanded = false }, // Bấm ra ngoài thì đóng
                modifier = Modifier.background(Color.White)
            ) {
                // Item 1: Hồ sơ
                DropdownMenuItem(
                    text = { Text("Hồ sơ cá nhân") },
                    onClick = {
                        isMenuExpanded = false
                        onProfileClick()
                    },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                // Item 2: Cài đặt
                DropdownMenuItem(
                    text = { Text("Cài đặt") },
                    onClick = {
                        isMenuExpanded = false
                        onSettingsClick()
                    },
                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )

                HorizontalDivider() // Kẻ vạch ngăn cách

                // Item 3: Đăng xuất
                DropdownMenuItem(
                    text = { Text("Đăng xuất", color = Color.Red, fontWeight = FontWeight.Bold) },
                    onClick = {
                        isMenuExpanded = false
                        onLogoutClick()
                    },
                    leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) }
                )
            }
        }

        // --- PHẦN PHẢI: NÚT GIỎ HÀNG (GIỮ NGUYÊN) ---
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