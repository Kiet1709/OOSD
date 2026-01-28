package com.example.foodelivery.presentation.driver.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.driver.profile.contract.DriverProfileEffect
import com.example.foodelivery.presentation.driver.profile.contract.DriverProfileIntent
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileScreen(
    navController: NavController,
    viewModel: DriverProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DriverProfileEffect.NavigateToEditProfile -> navController.navigate(Route.DriverEditProfile.path)
                is DriverProfileEffect.NavigateBack -> navController.popBackStack()
                is DriverProfileEffect.NavigateToLogin -> navController.navigate(Route.Login.path) { popUpTo(0) { inclusive = true } }
                is DriverProfileEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                // 👇 Điều hướng sang đổi mật khẩu
                is DriverProfileEffect.NavigateToChangePassword -> navController.navigate(Route.ChangePassword.path)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ tài xế", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(DriverProfileIntent.ClickBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9F9F9) // Màu nền xám nhẹ
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // --- PHẦN 1: HEADER (Avatar + Tên) ---
                AsyncImage(
                    model = state.user?.avatarUrl?.ifBlank { "https://i.pravatar.cc/300" } ?: "https://i.pravatar.cc/300",
                    contentDescription = "Driver Avatar",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.user?.name ?: "Tài xế",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Đối tác giao hàng",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- PHẦN 2: THÔNG TIN CÁ NHÂN ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text("Thông tin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileInfoRow(icon = Icons.Outlined.Email, label = "Email", value = state.user?.email ?: "---")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                    ProfileInfoRow(icon = Icons.Outlined.Phone, label = "Số điện thoại", value = state.user?.phoneNumber ?: "---")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- PHẦN 3: CÀI ĐẶT & HÀNH ĐỘNG ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                ) {
                    // Chỉnh sửa hồ sơ
                    ProfileMenuRow(
                        icon = Icons.Outlined.Edit,
                        title = "Chỉnh sửa hồ sơ",
                        onClick = { viewModel.setEvent(DriverProfileIntent.EditProfile) }
                    )

                    HorizontalDivider(color = Color(0xFFF0F0F0))

                    // Đổi mật khẩu
                    ProfileMenuRow(
                        icon = Icons.Outlined.Lock,
                        title = "Đổi mật khẩu",
                        onClick = { viewModel.setEvent(DriverProfileIntent.ClickChangePassword) }
                    )

                    HorizontalDivider(color = Color(0xFFF0F0F0))

                    // Đăng xuất
                    ProfileMenuRow(
                        icon = Icons.Outlined.Logout,
                        title = "Đăng xuất",
                        isDestructive = true,
                        onClick = { viewModel.setEvent(DriverProfileIntent.ClickLogout) }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// --- Các Component con dùng để tái sử dụng ---

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF5F5F5), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) Color.Red else PrimaryColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = if (isDestructive) Color.Red else Color.Black,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}