package com.example.foodelivery.presentation.restaurant.profile.edit

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodelivery.presentation.restaurant.profile.edit.contract.RestaurantEditProfileEffect
import com.example.foodelivery.presentation.restaurant.profile.edit.contract.RestaurantEditProfileIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantEditProfileScreen(
    navController: NavController,
    viewModel: RestaurantEditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RestaurantEditProfileEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is RestaurantEditProfileEffect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin", fontWeight = FontWeight.Bold) },
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
                onValueChange = { viewModel.setEvent(RestaurantEditProfileIntent.OnNameChange(it)) },
                label = { Text("Tên nhà hàng") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.setEvent(RestaurantEditProfileIntent.OnAddressChange(it)) },
                label = { Text("Địa chỉ") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { viewModel.setEvent(RestaurantEditProfileIntent.OnPhoneNumberChange(it)) },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.avatarUrl,
                onValueChange = { viewModel.setEvent(RestaurantEditProfileIntent.OnAvatarUrlChange(it)) },
                label = { Text("URL ảnh đại diện") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.coverPhotoUrl,
                onValueChange = { viewModel.setEvent(RestaurantEditProfileIntent.OnCoverPhotoUrlChange(it)) },
                label = { Text("URL ảnh bìa") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.setEvent(RestaurantEditProfileIntent.SaveChanges) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Lưu thay đổi")
                }
            }
        }
    }
}