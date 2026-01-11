package com.example.foodelivery.presentation.customer.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodelivery.domain.model.User
import com.example.foodelivery.presentation.customer.profile.contract.*
import com.example.foodelivery.ui.theme.PrimaryColor
import com.example.foodelivery.ui.theme.navigation.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerProfileScreen(
    navController: NavController,
    viewModel: CustomerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is ProfileEffect.ShowToast -> Toast.makeText(context, effect.msg, Toast.LENGTH_SHORT).show()
                ProfileEffect.NavigateBack -> navController.popBackStack()

                ProfileEffect.NavigateToLogin -> {
                    navController.navigate(Route.Login.path) {
                        popUpTo(0) { inclusive = true }
                    }
                }

                ProfileEffect.NavigateToEditProfile -> {
                    navController.navigate(Route.CustomerEditProfile.path)
                }

                ProfileEffect.NavigateToAddressList -> {
                    navController.navigate(Route.CustomerAddress.path)
                }
                ProfileEffect.NavigateToOrderHistory -> {
                    navController.navigate(Route.CustomerOrderHistory.path)
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hồ sơ cá nhân", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(ProfileIntent.ClickBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
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
                // 1. Header Avatar
                item {
                    ProfileHeaderSection(
                        user = state.user,
                        onEditClick = { viewModel.setEvent(ProfileIntent.ClickEditProfile) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 2. Menu Items
                item {
                    SectionTitle("Tài khoản của tôi")
                    ProfileOptionItem(Icons.Outlined.ShoppingBag, "Lịch sử đơn hàng") {
                        viewModel.setEvent(ProfileIntent.ClickOrderHistory)
                    }
                    ProfileOptionItem(Icons.Outlined.LocationOn, "Sổ địa chỉ") {
                        viewModel.setEvent(ProfileIntent.ClickAddress)
                    }
                    ProfileOptionItem(Icons.Outlined.Payment, "Thanh toán") {
                        viewModel.setEvent(ProfileIntent.ClickPaymentMethods)
                    }
                }

                item {
                    SectionTitle("Hỗ trợ")
                    ProfileOptionItem(Icons.Outlined.HeadsetMic, "Trung tâm trợ giúp") {
                        viewModel.setEvent(ProfileIntent.ClickSupport)
                    }
                    ProfileOptionItem(Icons.Outlined.Logout, "Đăng xuất", isDestructive = true) {
                        viewModel.setEvent(ProfileIntent.ClickLogout)
                    }
                }

                item {
                    Box(Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
                        Text("Phiên bản ${state.appVersion}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// --- CÁC COMPONENT CON ---

@Composable
fun ProfileHeaderSection(user: User?, onEditClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = user?.coverPhotoUrl?.ifBlank { "https://picsum.photos/400/200" } ?: "https://picsum.photos/400/200",
            contentDescription = "Cover photo",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                AsyncImage(
                    model = user?.avatarUrl?.ifBlank { "https://i.pravatar.cc/150" } ?: "https://i.pravatar.cc/150",
                    contentDescription = null,
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 4.dp, y = 4.dp).background(PrimaryColor, CircleShape).size(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = user?.name ?: "Khách hàng", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = user?.email ?: "Chưa cập nhật email", color = Color.Gray)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = if (isDestructive) Color.Red else Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = if (isDestructive) Color.Red else Color.Black,
            fontWeight = if (isDestructive) FontWeight.Bold else FontWeight.Normal
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp, modifier = Modifier.padding(start = 56.dp))
}