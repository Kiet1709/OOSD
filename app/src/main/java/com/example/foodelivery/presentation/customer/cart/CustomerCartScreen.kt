package com.example.foodelivery.presentation.customer.cart

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.presentation.customer.cart.components.CartBillSummary
import com.example.foodelivery.presentation.customer.cart.components.CartItemCard
import com.example.foodelivery.presentation.customer.cart.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerCartScreen(
    navController: NavController,
    viewModel: CustomerCartViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is CartEffect.ShowToast -> Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                is CartEffect.NavigateToHome -> navController.popBackStack()
                is CartEffect.NavigateToTracking -> {
                    navController.navigate("customer_tracking/${effect.orderId}")
                }
                is CartEffect.NavigateToCheckout -> {
                    navController.navigate(Route.Checkout.path) // Simplified navigation
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Giỏ Hàng (${state.items.size})", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (!state.isCartEmpty) {
                CartBottomBar(state.finalTotal) { viewModel.setEvent(CartIntent.ClickCheckout) }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else if (state.isCartEmpty) {
            EmptyState(modifier = Modifier.padding(padding)) { viewModel.setEvent(CartIntent.ClickGoHome) }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AddressInputSection(
                        address = state.address,
                        onValueChange = { viewModel.setEvent(CartIntent.UpdateAddress(it)) }
                    )
                }

                items(state.items, key = { it.foodId }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { viewModel.setEvent(CartIntent.IncreaseQty(item.foodId)) },
                        onDecrease = { viewModel.setEvent(CartIntent.DecreaseQty(item.foodId)) },
                        onRemove = { viewModel.setEvent(CartIntent.RemoveItem(item.foodId)) }
                    )
                }

                item {
                    CartBillSummary(state.subTotal, state.deliveryFee, state.discountAmount, state.finalTotal)
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun AddressInputSection(address: String, onValueChange: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor)
                Spacer(Modifier.width(8.dp))
                Text("Địa chỉ nhận hàng", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = address,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Số nhà, tên đường...") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    focusedLabelColor = PrimaryColor,
                    cursorColor = PrimaryColor
                )
            )
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier, onGoHome: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(100.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Giỏ hàng trống", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onGoHome,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Tiếp tục mua sắm")
        }
    }
}

@Composable
fun CartBottomBar(total: Double, onCheckout: () -> Unit) {
    Surface(shadowElevation = 16.dp, color = Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tổng cộng", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(total.toVndCurrency(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = PrimaryColor)
            }
            Button(
                onClick = onCheckout,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                modifier = Modifier.height(50.dp).width(160.dp)
            ) {
                Text("Thanh Toán", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
