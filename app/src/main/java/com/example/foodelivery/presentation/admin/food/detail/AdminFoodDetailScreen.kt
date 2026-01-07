package com.example.foodelivery.presentation.admin.food.detail

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.admin.food.detail.contract.FoodDetailEffect
import com.example.foodelivery.presentation.admin.food.detail.contract.FoodDetailIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFoodDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminFoodDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Image Picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.sendIntent(FoodDetailIntent.ImageSelected(uri))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is FoodDetailEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                FoodDetailEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Sửa Món Ăn" else "Thêm Món Ăn") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Preview & Pick
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { 
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
            ) {
                val model = state.selectedImageUri ?: state.serverImageUrl ?: "https://via.placeholder.com/300"
                AsyncImage(
                    model = model,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text("Chạm để chọn ảnh", modifier = Modifier.padding(8.dp), color = MaterialTheme.colorScheme.primary)
            }

            // [MỚI] Nhập URL ảnh
            OutlinedTextField(
                value = state.serverImageUrl ?: "",
                onValueChange = { viewModel.sendIntent(FoodDetailIntent.ImageUrlChanged(it)) },
                label = { Text("Link ảnh (URL)") },
                placeholder = { Text("https://example.com/image.jpg") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.sendIntent(FoodDetailIntent.NameChanged(it)) },
                label = { Text("Tên món") },
                isError = state.nameError != null,
                supportingText = { state.nameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.sendIntent(FoodDetailIntent.DescriptionChanged(it)) },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = state.price,
                onValueChange = { viewModel.sendIntent(FoodDetailIntent.PriceChanged(it)) },
                label = { Text("Giá (VNĐ)") },
                isError = state.priceError != null,
                supportingText = { state.priceError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // [MỚI] Chọn Danh Mục - Dropdown Menu gọn gàng
            var expanded by remember { mutableStateOf(false) }
            val selectedCategory = state.categories.find { it.id == state.selectedCategoryId }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Danh mục") },
                    placeholder = { Text("Chọn danh mục") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (state.categories.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Không có danh mục") },
                            onClick = { expanded = false }
                        )
                    } else {
                        state.categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.name) },
                                onClick = {
                                    viewModel.sendIntent(FoodDetailIntent.CategorySelected(cat.id))
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.sendIntent(FoodDetailIntent.ClickSubmit) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
                } else {
                    Text(if (state.isEditMode) "CẬP NHẬT" else "TẠO MỚI")
                }
            }
        }
    }
}