package com.example.foodelivery.presentation.restaurant.order_list

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.foodelivery.presentation.restaurant.order_list.components.RestaurantOrderItemCard
import com.example.foodelivery.presentation.restaurant.order_list.contract.RestaurantOrderListEffect
import com.example.foodelivery.presentation.restaurant.order_list.contract.RestaurantOrderListIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantOrderListScreen(
    navController: NavController,
    viewModel: RestaurantOrderListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RestaurantOrderListEffect.NavigateToOrderDetail -> {
                    Toast.makeText(context, "View order: ${effect.orderId}", Toast.LENGTH_SHORT).show()
                }
                is RestaurantOrderListEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.orders) { order ->
                        RestaurantOrderItemCard(order = order) { orderId, newStatus ->
                            viewModel.setEvent(RestaurantOrderListIntent.ChangeOrderStatus(orderId, newStatus))
                        }
                    }
                }
            }
        }
    }
}