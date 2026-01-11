package com.example.foodelivery.presentation.admin.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.admin.home.components.DashboardActionMenu
import com.example.foodelivery.presentation.admin.home.components.MenuItemUiModel
import com.example.foodelivery.presentation.admin.home.contract.AdminDashboardEffect
import com.example.foodelivery.presentation.admin.home.contract.AdminDashboardIntent
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class) // Needed for TopAppBar
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is AdminDashboardEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is AdminDashboardEffect.NavigateToLogin -> {
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is AdminDashboardEffect.NavigateToManageUsers -> { /* TODO */ }
                is AdminDashboardEffect.NavigateToManageDrivers -> { /* TODO */ }
                is AdminDashboardEffect.NavigateToManageRestaurants -> { /* TODO */ }
                is AdminDashboardEffect.NavigateToCategoryList -> navController.navigate(Route.AdminCategoryList.path)
                else -> {}
            }
        }
    }

    val menuItems = listOf(
        MenuItemUiModel("Quản lý Người Dùng", Icons.Outlined.People, Color(0xFF2196F3)) {
            viewModel.setEvent(AdminDashboardIntent.ClickManageUsers)
        },
        MenuItemUiModel("Quản lý Tài Xế", Icons.Outlined.TwoWheeler, Color(0xFF009688)) {
            viewModel.setEvent(AdminDashboardIntent.ClickManageDrivers)
        },
        MenuItemUiModel("Quản lý Nhà Hàng", Icons.Outlined.Store, Color(0xFFF44336)) {
            viewModel.setEvent(AdminDashboardIntent.ClickManageRestaurants)
        },
        MenuItemUiModel("Quản lý Danh Mục", Icons.Outlined.Category, Color(0xFF673AB7)) {
            viewModel.setEvent(AdminDashboardIntent.ClickManageCategory)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trang quản trị", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.setEvent(AdminDashboardIntent.ClickLogout) }) {
                        Icon(Icons.Outlined.Logout, contentDescription = "Đăng xuất", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp) // Add padding for content
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            DashboardActionMenu(menuItems = menuItems)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}