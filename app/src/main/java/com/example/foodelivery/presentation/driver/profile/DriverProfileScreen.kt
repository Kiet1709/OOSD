package com.example.foodelivery.presentation.driver.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.driver.profile.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route // Đảm bảo import Route đúng

// [QUAN TRỌNG] Import các component bạn đã tạo
import com.example.foodelivery.presentation.customer.profile.components.ProfileHeader
import com.example.foodelivery.presentation.customer.profile.components.ProfileMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileScreen(
    navController: NavController,
    viewModel: DriverProfileViewModel = hiltViewModel()
) {
    // 1. Lấy State & Context
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 2. Xử lý Logic & Navigation (Side Effects)
    LaunchedEffect(Unit) {

        viewModel.effect.collect { effect ->
            when(effect) {
                is DriverProfileEffect.ShowToast -> {
                    Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                }

                DriverProfileEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                DriverProfileEffect.NavigateToEditProfile -> {
                    navController.navigate(Route.DriverEditProfile.path)
                }

                DriverProfileEffect.NavigateToLogin -> {
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    // 3. Giao diện chính
    Scaffold(
        containerColor = Color(0xFFF5F5F5), // Màu nền xám nhẹ hiện đại
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Tài khoản Tài xế", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(DriverProfileIntent.ClickBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        // Loading State
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // --- PHẦN 1: HEADER (Avatar + Tên) ---
                item {
                    // Sử dụng ProfileHeader chuẩn
                    ProfileHeader(
                        user = state.user, // Truyền đúng User model của Driver
                        onEditClick = { viewModel.setEvent(DriverProfileIntent.ClickEditProfile) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // --- PHẦN 2: THÔNG TIN TÀI KHOẢN ---
                item {
                    SectionTitle("Tài khoản")

                    // Mục này cũng dẫn đến trang Edit Profile -> Tiện lợi cho user
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        title = "Hồ sơ cá nhân",
                        subtitle = "Chỉnh sửa tên, số điện thoại, khu vực",
                        onClick = { viewModel.setEvent(DriverProfileIntent.ClickEditProfile) }
                    )
                }

                // --- PHẦN 3: QUẢN LÝ CÔNG VIỆC (Đặc thù Tài xế) ---
                item {
                    SectionTitle("Công việc & Thu nhập")

                    ProfileMenuItem(
                        icon = Icons.Default.DirectionsCar,
                        title = "Thông tin phương tiện",
                        onClick = { Toast.makeText(context, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show() }
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 0.5.dp)

                    ProfileMenuItem(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "Thống kê thu nhập",
                        onClick = { Toast.makeText(context, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show() }
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 0.5.dp)

                    ProfileMenuItem(
                        icon = Icons.Default.History,
                        title = "Lịch sử chuyến đi",
                        onClick = { Toast.makeText(context, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show() }
                    )
                }

                // --- PHẦN 4: HỆ THỐNG ---
                item {
                    SectionTitle("Hệ thống")

                    ProfileMenuItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Cài đặt thông báo",
                        onClick = { Toast.makeText(context, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show() }
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 0.5.dp)

                    ProfileMenuItem(
                        icon = Icons.Outlined.HeadsetMic,
                        title = "Trung tâm hỗ trợ",
                        onClick = { Toast.makeText(context, "Đang kết nối tổng đài...", Toast.LENGTH_SHORT).show() }
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.2f), thickness = 0.5.dp)

                    // Nút Đăng xuất
                    ProfileMenuItem(
                        icon = Icons.Outlined.Logout,
                        title = "Đăng xuất",
                        textColor = Color.Red,
                        iconColor = Color.Red,
                        showArrow = false,
                        onClick = { viewModel.setEvent(DriverProfileIntent.ClickLogout) }
                    )
                }

                // --- FOOTER: VERSION ---
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Driver App v1.0.0",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Component tiêu đề section nhỏ (giữ lại cho gọn file)
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}