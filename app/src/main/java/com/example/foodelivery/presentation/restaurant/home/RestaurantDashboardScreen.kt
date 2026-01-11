package com.example.foodelivery.presentation.restaurant.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.admin.home.components.DashboardActionMenu
import com.example.foodelivery.presentation.admin.home.components.DashboardHeader
import com.example.foodelivery.presentation.admin.home.components.MenuItemUiModel
import com.example.foodelivery.presentation.restaurant.home.components.RevenueSummaryCard
import com.example.foodelivery.presentation.restaurant.home.contract.RestaurantDashboardEffect
import com.example.foodelivery.presentation.restaurant.home.contract.RestaurantDashboardIntent
import com.example.foodelivery.ui.theme.navigation.Route

@Composable
fun RestaurantDashboardScreen(
    navController: NavController, 
    viewModel: RestaurantDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is RestaurantDashboardEffect.NavigateToLogin -> {
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    val menuItems = listOf(
        MenuItemUiModel("Quản lý Thực đơn", Icons.Outlined.RestaurantMenu, Color(0xFFE91E63)) {
            navController.navigate(Route.RestaurantFoodList.path)
        },
        MenuItemUiModel("Quản lý Đơn hàng", Icons.Outlined.ReceiptLong, Color(0xFFFF9800)) {
            navController.navigate("restaurant_order_list")
        },
    )

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp)
        ) {
            DashboardHeader(
                headerTitle = "",
                adminName = state.restaurantName,
                avatarUrl = state.avatarUrl,
                hasUnreadNotifications = false,
                onNotificationClick = {},
                onProfileClick = { navController.navigate(Route.RestaurantProfile.path) }, 
                onChangePasswordClick = {},
                onSettingsClick = {},
                onHelpClick = {},
                onLogoutClick = { viewModel.setEvent(RestaurantDashboardIntent.ClickLogout) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            DashboardActionMenu(menuItems = menuItems)

            Spacer(modifier = Modifier.height(32.dp))

            RevenueSummaryCard(todayRevenue = state.todayRevenue, totalRevenue = state.totalRevenue)
        }
    }
}