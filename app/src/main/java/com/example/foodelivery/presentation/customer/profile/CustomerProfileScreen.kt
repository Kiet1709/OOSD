package com.example.foodelivery.presentation.customer.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import com.example.foodelivery.presentation.customer.profile.components.ProfileHeader
import com.example.foodelivery.presentation.customer.profile.components.ProfileMenuItem
import com.example.foodelivery.presentation.customer.profile.contract.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Import icon quay lại
// import com.example.foodelivery.ui.theme.navigation.Route // Import Route của bạn

@OptIn(ExperimentalMaterial3Api::class) // Cần để sử dụng TopAppBar
@Composable
fun CustomerProfileScreen(
    navController: NavController,
    viewModel: CustomerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle Side Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is ProfileEffect.ShowToast -> Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                is ProfileEffect.NavigateToLogin -> {
                    Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                    // Điều hướng về Login
                    navController.popBackStack()
                    // navController.navigate(Route.Login.path) { popUpTo(0) { inclusive = true } }
                }
                is ProfileEffect.NavigateToEditProfile -> {
                    Toast.makeText(context, "Mở màn hình chỉnh sửa...", Toast.LENGTH_SHORT).show()
                    // navController.navigate(Route.CustomerProfileEdit.path)
                }
                is ProfileEffect.NavigateToAddressList -> { /* Navigate */ }
                is ProfileEffect.NavigateToOrderHistory -> { /* Navigate */ }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
                topBar = {
            CenterAlignedTopAppBar( // Hoặc TopAppBar thường
                title = {
                    Text(
                        "Hồ sơ cá nhân",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) { // Lệnh quay lại trang trước
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        if (state.isLoading && state.user == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // 1. Header (Avatar + Info)
                item {
                    state.user?.let { user ->
                        ProfileHeader(
                            user = user,
                            onEditClick = { viewModel.setEvent(ProfileIntent.ClickEditProfile) }
                        )
                    }
                    Divider(thickness = 8.dp, color = Color(0xFFF0F0F0))
                }

                // 2. Section: Tài khoản
                item {
                    SectionHeader("Tài khoản của tôi")

                    ProfileMenuItem(
                        icon = Icons.Outlined.ShoppingBag,
                        title = "Lịch sử đơn hàng",
                        onClick = { viewModel.setEvent(ProfileIntent.ClickOrderHistory) }
                    )
                    DividerItem()

                    ProfileMenuItem(
                        icon = Icons.Outlined.LocationOn,
                        title = "Sổ địa chỉ",
                        subtitle = "Quản lý địa chỉ giao hàng",
                        onClick = { viewModel.setEvent(ProfileIntent.ClickAddress) }
                    )
                    DividerItem()

                    ProfileMenuItem(
                        icon = Icons.Outlined.AccountBalanceWallet,
                        title = "Phương thức thanh toán",
                        onClick = { viewModel.setEvent(ProfileIntent.ClickPaymentMethods) }
                    )
                    Divider(thickness = 8.dp, color = Color(0xFFF0F0F0))
                }

                // 3. Section: Tiện ích
                item {
                    SectionHeader("Tiện ích & Hỗ trợ")

                    ProfileMenuItem(
                        icon = Icons.Outlined.HeadsetMic,
                        title = "Trung tâm trợ giúp",
                        onClick = { viewModel.setEvent(ProfileIntent.ClickSupport) }
                    )
                    DividerItem()

                    ProfileMenuItem(
                        icon = Icons.Outlined.Language,
                        title = "Ngôn ngữ",
                        subtitle = "Tiếng Việt",
                        onClick = { /* Demo Change Language */ }
                    )
                    Divider(thickness = 8.dp, color = Color(0xFFF0F0F0))
                }

                // 4. Footer: Đăng xuất & Version
                item {
                    Spacer(modifier = Modifier.height(8.dp))


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Phiên bản ${state.appVersion}",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// Helper Components (Private)
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun DividerItem() {
    Divider(
        color = Color.LightGray,
        thickness = 0.5.dp,
        modifier = Modifier.padding(start = 56.dp) // Thụt lề cho đẹp
    )
}