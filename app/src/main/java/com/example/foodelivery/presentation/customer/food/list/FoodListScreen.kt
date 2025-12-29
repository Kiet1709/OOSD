package com.example.foodelivery.presentation.customer.food.list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.customer.food.list.contract.*
import com.example.foodelivery.presentation.customer.home.components.FoodCard // Tái sử dụng từ Home
import com.example.foodelivery.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen(
    navController: NavController,
    type: String, // popular, recommended, categoryId...
    viewModel: FoodListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Load data khi vào màn hình
    LaunchedEffect(type) {
        viewModel.setEvent(FoodListIntent.LoadList(type))
    }

    // Handle Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                FoodListEffect.NavigateBack -> navController.popBackStack()
                is FoodListEffect.NavigateToDetail -> navController.navigate("food_detail/${effect.id}")
                is FoodListEffect.ShowToast -> Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.title) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(FoodListIntent.ClickBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            if (state.foods.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có món ăn nào trong danh sách này.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(Color(0xFFF9F9F9))
                ) {
                    items(state.foods) { food ->
                        FoodCard(
                            food = food,
                            onClick = { viewModel.setEvent(FoodListIntent.ClickFood(food.id)) }
                        )
                    }
                }
            }
        }
    }
}