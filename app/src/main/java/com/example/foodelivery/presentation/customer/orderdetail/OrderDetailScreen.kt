package com.example.foodelivery.presentation.customer.orderdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foodelivery.core.common.Constants
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.domain.model.toColor
import com.example.foodelivery.domain.model.toVietnamese
import com.example.foodelivery.presentation.customer.orderdetail.components.OrderDetailItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String, 
    viewModel: OrderDetailViewModel = hiltViewModel(key = orderId)
) {
    val state by viewModel.uiState.collectAsState()
    val order = state.order

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (order != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Đơn hàng #${order.id.take(6).uppercase()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(order.status.toVietnamese(), color = order.status.toColor(), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Địa chỉ: ${order.shippingAddress}", style = MaterialTheme.typography.bodyMedium)
                    }

                    item {
                        Text("Các món đã đặt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }

                    items(order.items) { item ->
                        OrderDetailItem(item = item.toUiModel())
                    }

                    item {
                        com.example.foodelivery.presentation.customer.cart.components.CartBillSummary(
                            subTotal = order.totalPrice - Constants.DELIVERY_FEE,
                            deliveryFee = Constants.DELIVERY_FEE, 
                            discount = 0.0,
                            finalTotal = order.totalPrice
                        )
                    }
                }
            }
        }
    }
}

private fun com.example.foodelivery.domain.model.CartItem.toUiModel() = com.example.foodelivery.presentation.customer.cart.contract.CartItemUiModel(
    foodId = foodId,
    name = name,
    imageUrl = imageUrl,
    price = price,
    quantity = quantity,
    note = note,
    restaurantId = restaurantId
)
