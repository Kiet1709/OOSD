package com.example.foodelivery.presentation.restaurant.food.list

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.foodelivery.presentation.restaurant.food.list.components.FoodItemRow
import com.example.foodelivery.presentation.restaurant.food.list.contract.RestaurantFoodListEffect
import com.example.foodelivery.presentation.restaurant.food.list.contract.RestaurantFoodListIntent
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantFoodListScreen(
    navController: NavController,
    viewModel: RestaurantFoodListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RestaurantFoodListEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is RestaurantFoodListEffect.NavigateToEditFood -> {
                    navController.navigate(Route.RestaurantAddEditFood.createRoute(effect.foodId))
                }
                is RestaurantFoodListEffect.NavigateToAddFood -> {
                    navController.navigate(Route.RestaurantAddEditFood.createRoute("new"))
                }
            }
        }
    }

    if (state.foodToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.setEvent(RestaurantFoodListIntent.DismissDeleteDialog) },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa món '${state.foodToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.setEvent(RestaurantFoodListIntent.ConfirmDeleteFood) },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setEvent(RestaurantFoodListIntent.DismissDeleteDialog) }) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Thực đơn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.setEvent(RestaurantFoodListIntent.ClickAddFood) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Food", tint = Color.White)
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.foods) { food ->
                    FoodItemRow(
                        food = food,
                        onEditClick = { viewModel.setEvent(RestaurantFoodListIntent.ClickEditFood(food.id)) },
                        onDeleteClick = { viewModel.setEvent(RestaurantFoodListIntent.ClickDeleteFood(food)) }
                    )
                }
            }
        }
    }
}