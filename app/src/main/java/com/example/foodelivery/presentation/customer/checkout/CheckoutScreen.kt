package com.example.foodelivery.presentation.customer.checkout

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.customer.cart.components.CartBillSummary
import com.example.foodelivery.presentation.customer.cart.components.CartItemCard
import com.example.foodelivery.presentation.customer.checkout.contract.CheckoutEffect
import com.example.foodelivery.presentation.customer.checkout.contract.CheckoutIntent
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckoutEffect.NavigateToHome -> {
                    navController.navigate(Route.CustomerHome.path) {
                        popUpTo(Route.CustomerHome.path) { inclusive = true }
                    }
                }
                is CheckoutEffect.NavigateToTracking -> {
                    navController.navigate(Route.CustomerTracking.createRoute(effect.orderId)) {
                        popUpTo(Route.CustomerHome.path)
                    }
                }
                is CheckoutEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.setEvent(CheckoutIntent.ConfirmOrder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("XÁC NHẬN ĐẶT HÀNG", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Địa chỉ nhận hàng", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(state.address)
            }

            item {
                Text("Các món đã chọn", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(state.items) { item ->
                CartItemCard(item = item, onIncrease = {}, onDecrease = {}, onRemove = {})
            }

            item {
                CartBillSummary(
                    subTotal = state.subTotal,
                    deliveryFee = state.deliveryFee,
                    discount = 0.0, // Or get from state
                    finalTotal = state.finalTotal
                )
            }
        }
    }
}
