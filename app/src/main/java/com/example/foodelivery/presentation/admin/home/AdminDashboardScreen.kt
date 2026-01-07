package com.example.foodelivery.presentation.admin.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.presentation.admin.home.components.*
import com.example.foodelivery.presentation.admin.home.contract.AdminDashboardEffect
import com.example.foodelivery.presentation.admin.home.contract.AdminDashboardIntent
import com.example.foodelivery.ui.theme.navigation.Route

@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 1. NAVIGATION HANDLER
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is AdminDashboardEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()

                is AdminDashboardEffect.NavigateToLogin -> {
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }

                is AdminDashboardEffect.NavigateToManageOrders -> navController.navigate(Route.AdminOrderList.path)
                is AdminDashboardEffect.NavigateToFoodList -> navController.navigate(Route.AdminFoodList.path)
                is AdminDashboardEffect.NavigateToProfile -> navController.navigate(Route.AdminStoreInfo.path) // Đã sửa NavigateToProfile thành StoreInfo

                else -> {}
            }
        }
    }

    // 2. MENU ITEMS
    val menuItems = listOf(
        MenuItemUiModel("Đơn Hàng", Icons.Outlined.ReceiptLong, Color(0xFFFF9800)) {
            viewModel.setEvent(AdminDashboardIntent.ClickManageOrders)
        },
        MenuItemUiModel("Thực Đơn", Icons.Outlined.RestaurantMenu, Color(0xFFE91E63)) {
            viewModel.setEvent(AdminDashboardIntent.ClickManageFood)
        },
        MenuItemUiModel("Thông Tin Quán", Icons.Outlined.Store, Color(0xFF673AB7)) { // Thêm mục Thông tin quán
            viewModel.setEvent(AdminDashboardIntent.ClickProfile)
        },
        
        MenuItemUiModel("Tài Xế", Icons.Outlined.TwoWheeler, Color(0xFF009688)) {
            viewModel.setEvent(AdminDashboardIntent.ClickManageDrivers)
        },
        MenuItemUiModel("Người Dùng", Icons.Outlined.People, Color(0xFF2196F3)) {
            viewModel.setEvent(AdminDashboardIntent.ClickManageUsers)
        },
        MenuItemUiModel("Khuyến Mãi", Icons.Outlined.LocalOffer, Color(0xFF00BCD4)) {
            viewModel.setEvent(AdminDashboardIntent.ClickPromotions)
        },
        MenuItemUiModel("Đánh Giá", Icons.Outlined.StarOutline, Color(0xFFFFC107)) {
            viewModel.setEvent(AdminDashboardIntent.ClickReviews)
        },
        MenuItemUiModel("Báo Cáo", Icons.Outlined.BarChart, Color(0xFF4CAF50)) {
            viewModel.setEvent(AdminDashboardIntent.ClickReports)
        }
    )

    // 3. UI RENDER
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            DashboardHeader(
                adminName = state.adminName,
                avatarUrl = state.avatarUrl,
                hasUnreadNotifications = state.notificationCount > 0,
                onNotificationClick = { Toast.makeText(context, "Thông báo", Toast.LENGTH_SHORT).show() },
                onProfileClick = { viewModel.setEvent(AdminDashboardIntent.ClickProfile) },
                onChangePasswordClick = { },
                onSettingsClick = { viewModel.setEvent(AdminDashboardIntent.ClickSettings) },
                onHelpClick = { },
                onLogoutClick = { viewModel.setEvent(AdminDashboardIntent.ClickLogout) }
            )

            // Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Doanh thu",
                    value = state.todayRevenue.toVndCurrency(),
                    icon = Icons.Default.AttachMoney,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Đơn cần làm",
                    value = "${state.pendingOrders}",
                    icon = Icons.Default.ShoppingBag,
                    color = Color(0xFFE91E63),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Grid
            DashboardActionMenu(menuItems = menuItems)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}