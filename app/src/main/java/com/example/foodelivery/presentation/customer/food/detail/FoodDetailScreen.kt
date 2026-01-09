package com.example.foodelivery.presentation.customer.food.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.presentation.customer.food.detail.Contract.*
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route // ✅ THÊM IMPORT

@Composable
fun FoodDetailScreen(
    navController: NavController,
    foodId: String,
    viewModel: FoodDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(foodId) {
        viewModel.setEvent(FoodDetailIntent.LoadDetail(foodId))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                FoodDetailEffect.NavigateBack -> navController.popBackStack()
                is FoodDetailEffect.ShowToast -> Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                FoodDetailEffect.NavigateToCart -> {
                    navController.navigate(Route.CustomerCart.path)
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (!state.isLoading && state.food != null) {
                Button(
                    onClick = { viewModel.setEvent(FoodDetailIntent.ClickAddToCart) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Thêm vào giỏ - ${state.totalPrice.toVndCurrency()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryColor)
            } else {
                state.food?.let { food ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.height(280.dp).fillMaxWidth()) {
                            AsyncImage(
                                model = food.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { viewModel.setEvent(FoodDetailIntent.ClickBack) },
                                modifier = Modifier.padding(top = 40.dp, start = 16.dp).background(Color.White.copy(0.8f), CircleShape)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                            }
                        }
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(text = food.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                // [SỬA]: Thêm text cứng 20 min vì food chưa có time
                                Text(text = " ${food.rating} • 20 min • Giao hàng nhanh", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = "Mô tả", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Món ăn ngon tuyệt vời, hãy thử ngay!",
                                style = MaterialTheme.typography.bodyMedium, color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                FilledIconButton(
                                    onClick = { viewModel.setEvent(FoodDetailIntent.DecreaseQuantity) },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFF5F5F5)),
                                    modifier = Modifier.size(44.dp)
                                ) { Icon(Icons.Default.Remove, null, tint = Color.Black) }
                                Text(
                                    text = String.format("%02d", state.quantity),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                FilledIconButton(
                                    onClick = { viewModel.setEvent(FoodDetailIntent.IncreaseQuantity) },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = PrimaryColor),
                                    modifier = Modifier.size(44.dp)
                                ) { Icon(Icons.Default.Add, null, tint = Color.White) }
                            }
                        }
                    }
                }
            }
        }
    }
}