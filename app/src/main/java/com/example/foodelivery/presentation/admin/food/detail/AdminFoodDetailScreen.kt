package com.example.foodelivery.presentation.admin.food.detail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodelivery.presentation.admin.food.detail.components.FoodFormInputs
import com.example.foodelivery.presentation.admin.food.detail.components.FoodImagePicker
import com.example.foodelivery.presentation.admin.food.detail.contract.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFoodDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: FoodDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FoodDetailEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                FoodDetailEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Cập nhật món ăn" else "Thêm món mới") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.processIntent(FoodDetailIntent.ClickSubmit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White)
                else Text("LƯU MÓN ĂN", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Ảnh
            FoodImagePicker(
                selectedUri = state.selectedImageUri,
                serverUrl = state.serverImageUrl,
                onImageSelected = { viewModel.processIntent(FoodDetailIntent.ImageSelected(it)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Form Inputs
            FoodFormInputs(
                name = state.name,
                onNameChange = { viewModel.processIntent(FoodDetailIntent.NameChanged(it)) },
                nameError = state.nameError,
                price = state.price,
                onPriceChange = { viewModel.processIntent(FoodDetailIntent.PriceChanged(it)) },
                priceError = state.priceError,
                description = state.description,
                onDescriptionChange = { viewModel.processIntent(FoodDetailIntent.DescriptionChanged(it)) }
            )

            Spacer(modifier = Modifier.height(80.dp)) // Padding cho BottomBar
        }
    }
}