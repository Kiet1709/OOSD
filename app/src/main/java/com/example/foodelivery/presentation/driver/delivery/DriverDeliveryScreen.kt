package com.example.foodelivery.presentation.driver.delivery

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foodelivery.presentation.driver.delivery.components.DeliveryBottomSheet
import com.example.foodelivery.presentation.driver.delivery.components.DriverMapSection
import com.example.foodelivery.presentation.driver.delivery.contract.DeliveryEffect
import com.example.foodelivery.presentation.driver.delivery.contract.DeliveryIntent
import com.example.foodelivery.presentation.driver.delivery.contract.DeliveryOrderInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDeliveryScreen(
    navController: NavController,
    viewModel: DriverDeliveryViewModel = hiltViewModel()
) {
    // 1. STATE MANAGEMENT
    // Sử dụng collectAsState (hoặc collectAsStateWithLifecycle nếu có dependency)
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 2. SIDE EFFECTS (Xử lý sự kiện 1 lần)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DeliveryEffect.NavigateBackDashboard -> navController.popBackStack()
                is DeliveryEffect.ShowToast -> {
                    Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                }
                is DeliveryEffect.OpenDialer -> {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${effect.phone}"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // 3. UI LAYOUT
    Scaffold(
        // TopBar bán trong suốt để tăng trải nghiệm hiển thị bản đồ
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Đơn hàng #${state.order?.id?.takeLast(6)?.uppercase() ?: "..."}",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White.copy(alpha = 0.85f), // Hiệu ứng kính mờ
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                ),
                // Xử lý insets để không bị đè lên status bar
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        // Mở rộng nội dung xuống dưới bottom bar hệ thống
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- LAYER 1: MAP BACKGROUND ---
            // Nằm dưới cùng, chiếm toàn bộ diện tích
            DriverMapSection(
                progress = state.mapProgress,
                modifier = Modifier.fillMaxSize()
            )


            if (state.order != null) {
                val orderInfo = remember(state.order, state.customer, state.restaurant) {
                    DeliveryOrderInfo(
                        id = state.order!!.id,
                        restaurantName = state.restaurant?.name ?: "Nhà hàng đối tác",
                        restaurantAddress = state.restaurant?.address ?: "Đang cập nhật địa chỉ...",
                        customerName = state.customer?.name ?: "Khách hàng",
                        customerPhone = state.customer?.phoneNumber ?: "",
                        customerAddress = state.order!!.shippingAddress,
                        totalAmount = state.order!!.totalPrice,
                        note = ""
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 12.dp, vertical = 24.dp) // Padding cho đẹp mắt
                ) {
                    DeliveryBottomSheet(
                        order = orderInfo,
                        currentStep = state.currentStep,
                        onMainAction = { viewModel.setEvent(DeliveryIntent.ClickMainAction) },
                        onCall = { viewModel.setEvent(DeliveryIntent.ClickCallCustomer) },
                        onChat = { viewModel.setEvent(DeliveryIntent.ClickChatCustomer) },
                        onMapClick = { viewModel.setEvent(DeliveryIntent.ClickMapNavigation) }
                    )
                }
            }

            // --- LAYER 3: LOADING STATE ---
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}