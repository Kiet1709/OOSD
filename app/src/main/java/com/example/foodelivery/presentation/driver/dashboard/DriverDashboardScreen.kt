package com.example.foodelivery.presentation.driver.dashboard

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoMeals
import androidx.compose.material.icons.filled.OnlinePrediction
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foodelivery.presentation.driver.dashboard.components.AvailableOrderItemCard
import com.example.foodelivery.presentation.driver.dashboard.components.DriverDashboardHeader
import com.example.foodelivery.presentation.driver.dashboard.components.DriverHeader
import com.example.foodelivery.presentation.driver.dashboard.contract.DriverDashboardEffect
import com.example.foodelivery.presentation.driver.dashboard.contract.DriverDashboardIntent
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    viewModel: DriverDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DriverDashboardEffect.NavigateToDelivery -> {
                    navController.navigate(Route.DriverDelivery.createRoute(effect.orderId))
                }
                is DriverDashboardEffect.NavigateToLogin -> {
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is DriverDashboardEffect.NavigateToProfile -> {
                    navController.navigate(Route.DriverProfile.path)
                }
                is DriverDashboardEffect.ShowToast -> {
                    Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                }
                is DriverDashboardEffect.NavigateToChangePassword -> {
                    navController.navigate(Route.ChangePassword.path)
                }
                else -> {}
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            DriverHeader(
                user = state.user,
                isOnline = state.isOnline,
                onToggleStatus = { viewModel.setEvent(DriverDashboardIntent.ToggleOnlineStatus) }, // Sửa 'onStatusChange'
                onProfileClick = { viewModel.setEvent(DriverDashboardIntent.ClickProfile) },
                onChangePasswordClick = { viewModel.setEvent(DriverDashboardIntent.ClickChangePassword) },
                onLogoutClick = { viewModel.setEvent(DriverDashboardIntent.Logout) },
                onRevenueClick = { /* TODO: Xử lý sau */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (!state.isOnline) {
                EmptyState(icon = Icons.Default.OnlinePrediction, message = "Bạn đang offline. Hãy bật trạng thái để nhận đơn.")
            } else if (state.availableOrders.isEmpty()) {
                EmptyState(icon = Icons.Default.NoMeals, message = "Chưa có đơn hàng nào mới")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Text("Đơn hàng có sẵn", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                    items(state.availableOrders) { order ->
                        AvailableOrderItemCard(order = order) {
                            viewModel.setEvent(DriverDashboardIntent.AcceptOrder(order.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.padding(16.dp), tint = Color.Gray)
            Text(text = message, color = Color.Gray)
        }
    }
}
