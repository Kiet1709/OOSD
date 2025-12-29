package com.example.foodelivery.presentation.driver.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.presentation.driver.dashboard.components.DriverOrderCard
import com.example.foodelivery.presentation.driver.dashboard.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    viewModel: DriverDashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is DriverDashboardEffect.ShowToast -> Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                is DriverDashboardEffect.NavigateToDelivery -> {
                    // navController.navigate("driver_delivery_route/${effect.orderId}")
                    Toast.makeText(context, "Bắt đầu giao đơn: ${effect.orderId}", Toast.LENGTH_SHORT).show()
                }
                is DriverDashboardEffect.NavigateToRevenueReport -> { /* Navigate */ }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF2F2F2),
        topBar = {
            // Header: Doanh thu & Toggle Online
            Surface(color = Color.White, shadowElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .statusBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Doanh thu hôm nay", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        Text(
                            text = state.todayRevenue.toVndCurrency(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }

                    // Nút Online/Offline
                    Button(
                        onClick = { viewModel.setEvent(DriverDashboardIntent.ToggleOnlineStatus) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isOnline) Color.Green else Color.Red
                        ),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (state.isOnline) "ONLINE" else "OFFLINE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->

        if (!state.isOnline) {
            // Offline State
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Bạn đang Offline", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
                    Text("Bật Online để bắt đầu nhận đơn", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        } else if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else if (state.availableOrders.isEmpty()) {
            // Empty State (Online nhưng chưa có đơn)
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Đang tìm đơn hàng quanh đây...", color = Color.Gray, style = MaterialTheme.typography.bodyLarge)
                // Có thể thêm Animation Radar quét ở đây
            }
        } else {
            // List Orders
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Đơn hàng mới (${state.availableOrders.size})",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(state.availableOrders) { order ->
                    DriverOrderCard(
                        order = order,
                        onAccept = { viewModel.setEvent(DriverDashboardIntent.AcceptOrder(order.id)) },
                        onReject = { viewModel.setEvent(DriverDashboardIntent.RejectOrder(order.id)) }
                    )
                }
            }
        }
    }
}