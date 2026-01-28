package com.example.foodelivery.presentation.customer.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.customer.home.components.FoodCard
import com.example.foodelivery.presentation.customer.home.components.HomeHeader
import com.example.foodelivery.presentation.customer.home.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route

@Composable
fun CustomerHomeScreen(
    navController: NavController,
    viewModel: CustomerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.homeState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is CustomerHomeEffect.NavigateToFoodDetail ->
                    navController.navigate(Route.CustomerFoodDetail.createRoute(effect.foodId))
                is CustomerHomeEffect.NavigateToCategory ->
                    navController.navigate(Route.CustomerFoodList.createRoute(effect.categoryId))
                is CustomerHomeEffect.NavigateToFoodList ->
                    navController.navigate(Route.CustomerFoodList.createRoute(effect.type))
                CustomerHomeEffect.NavigateToCart -> navController.navigate(Route.CustomerCart.path)
                CustomerHomeEffect.NavigateToProfile -> navController.navigate(Route.CustomerProfile.path)
                CustomerHomeEffect.NavigateToSettings -> navController.navigate(Route.CustomerSettings.path)
                CustomerHomeEffect.NavigateToChangePassword ->
                    navController.navigate(Route.ChangePassword.path)
                CustomerHomeEffect.NavigateToLogin -> navController.navigate(Route.Login.path) { popUpTo(0) { inclusive = true } }
                CustomerHomeEffect.NavigateToOrderHistory -> navController.navigate(Route.CustomerOrderHistory.path) // Add this
                is CustomerHomeEffect.NavigateToTracking -> navController.navigate(Route.CustomerTracking.createRoute(effect.orderId))
                is CustomerHomeEffect.ShowToast -> Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            HomeHeader(
                user = state.user,
                modifier = Modifier.background(Color.White).statusBarsPadding(),
                onCartClick = { viewModel.setEvent(CustomerHomeIntent.ClickCart) },
                onProfileClick = { viewModel.setEvent(CustomerHomeIntent.ClickProfile) },
                onOrderClick = { viewModel.setEvent(CustomerHomeIntent.ClickCurrentOrder) },
                onSettingsClick = { viewModel.setEvent(CustomerHomeIntent.ClickSettings) },
                onChangePasswordClick = { viewModel.setEvent(CustomerHomeIntent.ClickChangePassword) },
                onLogoutClick = { viewModel.setEvent(CustomerHomeIntent.ClickLogout) }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            HomeSearchBar(
                text = searchText,
                onTextChange = { searchText = it },
                onSearchClicked = { /* TODO */ }
            )
            CategorySection(
                categories = state.categories,
                onClick = { viewModel.setEvent(CustomerHomeIntent.ClickCategory(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else {
                 LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.foods) { food ->
                        FoodCard(food = food, onClick = { viewModel.setEvent(CustomerHomeIntent.ClickFood(food)) })
                    }
                }
            }
        }
    }
}

@Composable
fun HomeSearchBar(text: String, onTextChange: (String) -> Unit, onSearchClicked: () -> Unit) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text("Tìm kiếm món ăn...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onSearchClicked() },
        singleLine = true
    )
}

@Composable
fun CategorySection(categories: List<CategoryUiModel>, onClick: (String) -> Unit) {
    Column {
        Text("Danh Mục", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(categories) { category ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick(category.id) }) {
                    Surface(shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp, modifier = Modifier.size(60.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(model = category.iconUrl, contentDescription = null, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = category.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}