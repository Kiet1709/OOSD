package com.example.foodelivery.presentation.customer.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.customer.home.components.HomeFoodSection
import com.example.foodelivery.presentation.customer.home.components.HomeHeader
import com.example.foodelivery.presentation.customer.home.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route

@Composable
fun CustomerHomeScreen(
    navController: NavController,
    viewModel: CustomerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }

    // Xử lý sự kiện (Navigation, Toast...)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is CustomerHomeEffect.NavigateToFoodDetail ->
                    navController.navigate(Route.CustomerFoodDetail.createRoute(effect.foodId))

                CustomerHomeEffect.NavigateToCart ->
                    navController.navigate(Route.CustomerCart.path)

                CustomerHomeEffect.NavigateToProfile ->
                    navController.navigate(Route.CustomerProfile.path)

                CustomerHomeEffect.NavigateToLogin ->
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true } // Logout xóa stack
                    }

                is CustomerHomeEffect.ShowToast ->
                    Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            // Header: Đã cập nhật đủ 4 hành động
            HomeHeader(
                userName = state.userName,
                avatarUrl = state.avatarUrl,
                modifier = Modifier.background(Color.White),

                // 1. Click Giỏ hàng
                onCartClick = { viewModel.setEvent(CustomerHomeIntent.ClickCart) },

                // 2. Click Profile (trong Menu)
                onProfileClick = { viewModel.setEvent(CustomerHomeIntent.ClickProfile) },

                // 3. Click Settings (trong Menu)
                onSettingsClick = { viewModel.setEvent(CustomerHomeIntent.ClickSettings) },

                // 4. Click Logout (trong Menu)
                onLogoutClick = { viewModel.setEvent(CustomerHomeIntent.ClickLogout) }
            )
        }
    )
    { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. Search Bar
                item {
                    HomeSearchBar(
                        text = searchText,
                        onTextChange = { newText -> searchText = newText },
                        onSearchClicked = { viewModel.setEvent(CustomerHomeIntent.ClickSearch) }
                    )
                }

                // 2. Categories
                item {
                    CategorySection(
                        categories = state.categories,
                        onClick = { viewModel.setEvent(CustomerHomeIntent.ClickCategory(it)) }
                    )
                }

                // 3. Popular Food
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HomeFoodSection(
                        title = "Món Ngon Nổi Bật",
                        foods = state.popularFoods,
                        onFoodClick = { viewModel.setEvent(CustomerHomeIntent.ClickFood(it)) }

                    )
                }

                // 4. Recommended Food
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HomeFoodSection(
                        title = "Gợi Ý Cho Bạn",
                        foods = state.recommendedFoods,
                        onFoodClick = { viewModel.setEvent(CustomerHomeIntent.ClickFood(it)) }
                    )
                }
            }
        }
    }
}

// --- Local Components (Search & Category) ---
@Composable
fun HomeSearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSearchClicked: () -> Unit
) {
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onSearchClicked() }, // Cho phép click vào toàn bộ search bar
        singleLine = true
    )
}

@Composable
fun CategorySection(
    categories: List<CategoryUiModel>,
    onClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Danh Mục",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onClick(category.id) }
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(60.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = category.iconUrl,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}