package com.example.foodelivery.presentation.driver.profile.editprofile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.driver.profile.editprofile.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverEditProfileScreen(
    navController: NavController,
    viewModel: DriverEditProfileViewModel = hiltViewModel()
) {
    // Lấy state từ ViewModel
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Lắng nghe các sự kiện phụ (Toast, Back)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is DriverEditProfileEffect.GoBack -> navController.popBackStack()
                is DriverEditProfileEffect.ShowToast -> {
                    Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hồ sơ Cá Nhân ",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(DriverEditProfileIntent.ClickBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.setEvent(DriverEditProfileIntent.ClickSave) }) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = PrimaryColor)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- AVATAR ---
            AsyncImage(
                // Thêm ?u=driver vào link ảnh giả để tạo avatar khác biệt khi test
                model = state.avatarUrl.ifBlank { "https://i.pravatar.cc/150?u=driver" },
                contentDescription = "Driver Avatar",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- FORM NHẬP LIỆU ---

            // 1. Họ tên
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.setEvent(DriverEditProfileIntent.ChangeName(it)) },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 2. Số điện thoại
            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.setEvent(DriverEditProfileIntent.ChangePhone(it)) },
                label = { Text("Số điện thoại liên hệ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 3. Địa chỉ
            // Với tài xế, địa chỉ này có thể hiểu là khu vực hoạt động
            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.setEvent(DriverEditProfileIntent.ChangeAddress(it)) },
                label = { Text("Khu vực hoạt động / Địa chỉ") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- BUTTON SAVE ---
            Button(
                onClick = { viewModel.setEvent(DriverEditProfileIntent.ClickSave) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                enabled = !state.isLoading // Disable nút khi đang load để tránh spam click
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("CẬP NHẬT HỒ SƠ", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}