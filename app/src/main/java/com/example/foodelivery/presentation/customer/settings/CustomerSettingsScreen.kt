package com.example.foodelivery.presentation.customer.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSettingsScreen(navController: NavController) {
    // State giả lập (Thực tế nên lấy từ ViewModel)
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFF8F9FA), // Màu nền xám nhẹ hiện đại
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Cài đặt",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- ĐÃ BỎ PHẦN HEADER USER INFO ---

            // 1. NHÓM TÀI KHOẢN
            item {
                SettingsSection(title = "Tài khoản") {
                    SettingsNavigationItem(
                        icon = Icons.Default.Person,
                        title = "Chỉnh sửa hồ sơ",
                        onClick = { navController.navigate(Route.CustomerEditProfile.path) }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.LocationOn,
                        title = "Sổ địa chỉ",
                        onClick = { navController.navigate(Route.CustomerAddress.path) }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Lock,
                        title = "Đổi mật khẩu",
                        onClick = { /* Navigate to Change Password */ }
                    )
                }
            }

            // 2. NHÓM CẤU HÌNH APP
            item {
                SettingsSection(title = "Cài đặt ứng dụng") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "Thông báo",
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                    SettingsSwitchItem(
                        icon = Icons.Default.DarkMode,
                        title = "Chế độ tối",
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Language,
                        title = "Ngôn ngữ",
                        valueText = "Tiếng Việt",
                        onClick = { /* Open Language Dialog */ }
                    )
                }
            }

            // 3. NHÓM KHÁC
            item {
                SettingsSection(title = "Khác") {
                    SettingsNavigationItem(
                        icon = Icons.AutoMirrored.Filled.Help,
                        title = "Trợ giúp & Hỗ trợ",
                        onClick = { }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Info,
                        title = "Về ứng dụng",
                        valueText = "v1.0.0",
                        onClick = { }
                    )
                }
            }

            // 4. NÚT ĐĂNG XUẤT
            item {
                Button(
                    onClick = {
                        // Xử lý đăng xuất -> Về Login và xóa hết BackStack cũ
                        navController.navigate(Route.Login.path) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE), // Màu đỏ nhạt
                        contentColor = Color.Red
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

// ==========================================
// CÁC COMPONENT CON (Helper Composables)
// ==========================================

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            content()
        }
    }
}

@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    valueText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(icon = icon)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

        if (valueText != null) {
            Text(text = valueText, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
    // Đường kẻ ngang phân cách
    HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(icon = icon)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryColor
            )
        )
    }
    HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
}

@Composable
fun IconBox(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryColor.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(20.dp)
        )
    }
}