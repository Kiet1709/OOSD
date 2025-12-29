package com.example.foodelivery.presentation.customer.tracking

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.customer.tracking.components.*
import com.example.foodelivery.presentation.customer.tracking.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerTrackingScreen(
    navController: NavController,
    orderId: String = "DEMO-123", // Nhận từ NavAgrs
    viewModel: CustomerTrackingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Init data khi vào màn hình
    LaunchedEffect(orderId) {
        viewModel.init(orderId)
    }

    // Handle Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is TrackingEffect.NavigateBack -> navController.popBackStack()
                is TrackingEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is TrackingEffect.OpenDialer -> {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${effect.phone}"))
                    context.startActivity(intent)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- 1. LAYER MAP (BACKGROUND) ---
        Column(Modifier.fillMaxSize()) {
            TrackingMapSection(
                progress = state.driverProgress,
                modifier = Modifier.weight(0.45f) // Map chiếm 45% màn hình
            )
            Spacer(modifier = Modifier.weight(0.55f)) // Chừa chỗ cho BottomSheet
        }

        // --- 2. LAYER TOP BAR ---
        SmallFloatingActionButton(
            onClick = { viewModel.setEvent(TrackingIntent.ClickBack) },
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier.padding(16.dp).statusBarsPadding()
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        // --- 3. LAYER BOTTOM SHEET (INFO) ---
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.60f), // Chiếm 60% dưới
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Handle bar (Thanh kéo ảo)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ETA (Thời gian dự kiến)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Thời gian dự kiến", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = state.eta,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Timeline
                TrackingTimeline(currentStep = state.currentStep)

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color(0xFFF0F0F0), thickness = 8.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Driver Info
                state.driver?.let { driver ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        DriverInfoCard(
                            driver = driver,
                            onCall = { viewModel.setEvent(TrackingIntent.ClickCallDriver) },
                            onMessage = { viewModel.setEvent(TrackingIntent.ClickMessageDriver) }
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}