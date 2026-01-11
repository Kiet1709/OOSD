package com.example.foodelivery.presentation.restaurant.food.detail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.restaurant.food.detail.contract.RestaurantAddEditFoodEffect
import com.example.foodelivery.presentation.restaurant.food.detail.contract.RestaurantAddEditFoodIntent
import com.example.foodelivery.presentation.restaurant.food.detail.contract.RestaurantAddEditFoodState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantAddEditFoodScreen(
    navController: NavController,
    viewModel: RestaurantAddEditFoodViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RestaurantAddEditFoodEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is RestaurantAddEditFoodEffect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Sửa Món Ăn" else "Thêm Món Ăn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.setEvent(RestaurantAddEditFoodIntent.NameChanged(it)) },
                label = { Text("Tên món ăn") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.nameError != null
            )
            if (state.nameError != null) {
                Text(state.nameError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.setEvent(RestaurantAddEditFoodIntent.DescriptionChanged(it)) },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.price,
                onValueChange = { viewModel.setEvent(RestaurantAddEditFoodIntent.PriceChanged(it)) },
                label = { Text("Giá") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.priceError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            if (state.priceError != null) {
                Text(state.priceError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = state.imageUrl,
                onValueChange = { viewModel.setEvent(RestaurantAddEditFoodIntent.ImageUrlChanged(it)) },
                label = { Text("URL hình ảnh") },
                modifier = Modifier.fillMaxWidth()
            )

            CategoryDropdown(state, viewModel)

            Button(
                onClick = { viewModel.setEvent(RestaurantAddEditFoodIntent.Submit) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(if (state.isEditMode) "Lưu thay đổi" else "Thêm món ăn")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(state: RestaurantAddEditFoodState, viewModel: RestaurantAddEditFoodViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategoryName = state.categories.find { it.id == state.categoryId }?.name ?: "Chọn danh mục"

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedCategoryName,
            onValueChange = {},
            label = { Text("Danh mục") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            state.categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        viewModel.setEvent(RestaurantAddEditFoodIntent.CategorySelected(category.id))
                        expanded = false
                    }
                )
            }
        }
    }
}