package com.example.foodelivery.presentation.admin.store_info

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.admin.store_info.contract.StoreInfoEffect
import com.example.foodelivery.presentation.admin.store_info.contract.StoreInfoIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStoreInfoScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminStoreInfoViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is StoreInfoEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                StoreInfoEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin cửa hàng") },
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
            // Hiển thị ảnh bìa nếu có
            if (state.coverUrl.isNotEmpty()) {
                AsyncImage(
                    model = state.coverUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.sendIntent(StoreInfoIntent.UpdateName(it)) },
                label = { Text("Tên cửa hàng") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.sendIntent(StoreInfoIntent.UpdateAddress(it)) },
                label = { Text("Địa chỉ") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.sendIntent(StoreInfoIntent.UpdatePhone(it)) },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.sendIntent(StoreInfoIntent.UpdateDescription(it)) },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Các trường nhập URL ảnh (có thể nâng cấp lên ImagePicker sau)
            OutlinedTextField(
                value = state.avatarUrl,
                onValueChange = { viewModel.sendIntent(StoreInfoIntent.UpdateAvatar(it)) },
                label = { Text("URL Ảnh đại diện") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.coverUrl,
                onValueChange = { viewModel.sendIntent(StoreInfoIntent.UpdateCover(it)) },
                label = { Text("URL Ảnh bìa") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.sendIntent(StoreInfoIntent.Save) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White)
                } else {
                    Text("LƯU THÔNG TIN")
                }
            }
        }
    }
}