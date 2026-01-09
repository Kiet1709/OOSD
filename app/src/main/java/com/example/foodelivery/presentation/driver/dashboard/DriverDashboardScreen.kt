package com.example.foodelivery.presentation.driver.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.driver.dashboard.components.DriverHeader
import com.example.foodelivery.presentation.driver.dashboard.components.DriverOrderCard
import com.example.foodelivery.presentation.driver.dashboard.components.RevenueCard
import com.example.foodelivery.presentation.driver.dashboard.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    viewModel: DriverDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Xử lý Side Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DriverDashboardEffect.ShowToast -> Toast.makeText(
                    context,
                    effect.msg,
                    Toast.LENGTH_SHORT
                ).show()

                is DriverDashboardEffect.NavigateToDelivery -> navController.navigate((Route.DriverDelivery.createRoute(effect.orderId)))
                is DriverDashboardEffect.NavigateToProfile -> navController.navigate(Route.DriverProfile.path)
                is DriverDashboardEffect.NavigateToRevenueReport -> Toast.makeText(
                    context,
                    "Màn hình chi tiết doanh thu",
                    Toast.LENGTH_SHORT
                ).show()

                // [SỬA LỖI]: Thêm nhánh xử lý NavigateToLogin
                is DriverDashboardEffect.NavigateToLogin -> {
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            DriverHeader(
                // [NÂNG CẤP SENIOR]: Thay thế user giả bằng user thật từ State (Firebase)
                user = state.user,

                isOnline = state.isOnline,
                onToggleStatus = { viewModel.setEvent(DriverDashboardIntent.ToggleOnlineStatus) },
                onProfileClick = { viewModel.setEvent(DriverDashboardIntent.ClickProfile) },
                onRevenueClick = { viewModel.setEvent(DriverDashboardIntent.ClickRevenueDetail) },
                onLogoutClick = { viewModel.setEvent(DriverDashboardIntent.Logout) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // SECTION 1: DOANH THU
            item {
                RevenueCard(
                    revenue = state.todayRevenue,
                    onClick = { viewModel.setEvent(DriverDashboardIntent.ClickRevenueDetail) }
                )
            }

            // SECTION 2: DANH SÁCH ĐƠN HÀNG
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Đơn hàng lân cận",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (state.isOnline) {
                        Badge(containerColor = PrimaryColor) {
                            Text(
                                text = "${state.availableOrders.size} đơn",
                                modifier = Modifier.padding(horizontal = 4.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // LOGIC HIỂN THỊ THEO TRẠNG THÁI
            if (!state.isOnline) {
                // State: Offline
                item {
                    EmptyStateContent(
                        title = "Bạn đang Offline",
                        message = "Bật trạng thái ONLINE để hệ thống bắt đầu tìm đơn cho bạn.",
                        isLoading = false
                    )
                }
            } else if (state.isLoading && state.availableOrders.isEmpty()) {
                // State: Loading
                item {
                    EmptyStateContent(
                        title = "Đang tìm đơn hàng...",
                        message = "Đang quét khu vực lân cận, vui lòng đợi trong giây lát.",
                        isLoading = true
                    )
                }
            } else if (state.availableOrders.isEmpty()) {
                // State: Empty
                item {
                    EmptyStateContent(
                        title = "Chưa có đơn hàng",
                        message = "Hiện tại chưa có đơn hàng nào quanh đây. Vui lòng di chuyển đến khu vực đông đúc hơn.",
                        isLoading = true
                    )
                }
            } else {
                // State: List Data
                items(state.availableOrders, key = { it.id }) { order ->
                    DriverOrderCard(
                        order = order,
                        onAccept = { viewModel.setEvent(DriverDashboardIntent.AcceptOrder(order.id)) },
                        onReject = { viewModel.setEvent(DriverDashboardIntent.RejectOrder(order.id)) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun EmptyStateContent(
    title: String,
    message: String,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = PrimaryColor,
                strokeWidth = 3.dp
            )
        } else {
            // Đã dùng Icon Vector an toàn để tránh crash
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}