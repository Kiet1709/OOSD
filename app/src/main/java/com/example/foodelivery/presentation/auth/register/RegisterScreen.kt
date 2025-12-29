package com.example.foodelivery.presentation.auth.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodelivery.presentation.auth.register.contract.RegisterEffect
import com.example.foodelivery.presentation.auth.register.contract.RegisterIntent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterScreen(
    onNavigation: (RegisterEffect.Navigation) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // [FIX]: Dùng uiState thay vì state (theo BaseViewModel Senior)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is RegisterEffect.Navigation -> {
                    onNavigation(effect)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tạo tài khoản",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Đăng ký để bắt đầu đặt món ngay",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Input Fields
            FoodDeliveryTextField(
                value = state.name,
                onValueChange = { viewModel.processIntent(RegisterIntent.NameChanged(it)) },
                label = "Họ và tên",
                errorMessage = state.nameError,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
            FoodDeliveryTextField(
                value = state.email,
                onValueChange = { viewModel.processIntent(RegisterIntent.EmailChanged(it)) },
                label = "Email",
                errorMessage = state.emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            FoodDeliveryTextField(
                value = state.phone,
                onValueChange = { viewModel.processIntent(RegisterIntent.PhoneChanged(it)) },
                label = "Số điện thoại",
                errorMessage = state.phoneError,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
            FoodDeliveryPasswordField(
                value = state.pass,
                onValueChange = { viewModel.processIntent(RegisterIntent.PasswordChanged(it)) },
                label = "Mật khẩu",
                errorMessage = state.passError,
                isVisible = state.isPasswordVisible,
                onToggleVisibility = { viewModel.processIntent(RegisterIntent.TogglePasswordVisibility) },
                imeAction = ImeAction.Next
            )
            FoodDeliveryPasswordField(
                value = state.confirmPass,
                onValueChange = { viewModel.processIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
                label = "Xác nhận mật khẩu",
                errorMessage = state.confirmPassError,
                isVisible = state.isConfirmPasswordVisible,
                onToggleVisibility = { viewModel.processIntent(RegisterIntent.ToggleConfirmPasswordVisibility) },
                imeAction = ImeAction.Done
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Button
            Button(
                onClick = { viewModel.processIntent(RegisterIntent.SubmitRegister) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("ĐĂNG KÝ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Đã có tài khoản?", color = Color.Gray)
                Text(
                    text = " Đăng nhập",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { viewModel.processIntent(RegisterIntent.ClickLogin) }
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Giữ nguyên các hàm FoodDeliveryTextField và FoodDeliveryPasswordField của bạn
// ...
@Composable
fun FoodDeliveryTextField(value: String, onValueChange: (String) -> Unit, label: String, errorMessage: String?, keyboardType: KeyboardType, imeAction: ImeAction) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        OutlinedTextField(
            value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction), shape = MaterialTheme.shapes.medium
        )
        if (errorMessage != null) Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
    }
}

@Composable
fun FoodDeliveryPasswordField(value: String, onValueChange: (String) -> Unit, label: String, errorMessage: String?, isVisible: Boolean, onToggleVisibility: () -> Unit, imeAction: ImeAction) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        OutlinedTextField(
            value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null, singleLine = true,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = { IconButton(onClick = onToggleVisibility) { Icon(if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction), shape = MaterialTheme.shapes.medium
        )
        if (errorMessage != null) Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp, top = 4.dp))
    }
}