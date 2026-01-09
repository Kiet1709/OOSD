package com.example.foodelivery.presentation.driver.delivery

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.driver.delivery.components.DeliveryBottomSheet
import com.example.foodelivery.presentation.driver.delivery.components.DriverMapSection
import com.example.foodelivery.presentation.driver.delivery.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor // Đảm bảo bạn có file theme màu

@Composable
fun DriverDeliveryScreen(
    navController: NavController,
    orderId: String,
    viewModel: DriverDeliveryViewModel = hiltViewModel()
) {
    // 1. Lắng nghe State từ ViewModel
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 2. Gửi Intent: Load đơn hàng khi màn hình mở
    LaunchedEffect(orderId) {
        if (orderId.isNotBlank()) {
            viewModel.setEvent(DeliveryIntent.LoadOrder(orderId))
        }
    }

    // 3. Xử lý Side Effects (Chuyển màn hình, Gọi điện, Toast)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is DeliveryEffect.NavigateBackDashboard -> {
                    navController.popBackStack()
                }
                is DeliveryEffect.OpenDialer -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${effect.phone}"))
                    context.startActivity(intent)
                }
                is DeliveryEffect.ShowToast -> {
                    Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 4. Giao diện chính (Dùng Box để xếp chồng các lớp)
    Box(modifier = Modifier.fillMaxSize()) {

        // Lớp 1: Bản đồ nền (Map)
        DriverMapSection(
            progress = state.mapProgress,
            modifier = Modifier.fillMaxSize()
        )

        // Lớp 2: Nút Back (Góc trên trái)
        SmallFloatingActionButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding()
                .align(Alignment.TopStart),
            containerColor = Color.White,
            contentColor = Color.Black
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        // Lớp 3: Loading hoặc BottomSheet
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            // Chỉ hiển thị BottomSheet khi đã có dữ liệu Order
            state.order?.let { orderInfo ->
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
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
        }
    }
}