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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodelivery.presentation.auth.register.contract.RegisterEffect
import com.example.foodelivery.presentation.auth.register.contract.RegisterIntent

@Composable
fun RegisterScreen(
    onNavigation: (RegisterEffect.Navigation) -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    preSelectedRole: String = "CUSTOMER" // Nhận tham số này từ Navigation
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Init Role khi vào màn hình
    LaunchedEffect(preSelectedRole) {
        viewModel.processIntent(RegisterIntent.SelectRole(preSelectedRole))
    }

    LaunchedEffect(true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is RegisterEffect.Navigation -> onNavigation(effect)
            }
        }
    }

    // Tiêu đề dựa trên role
    val titleText = when(state.role) {
        "DRIVER" -> "Đăng ký Tài xế"
        "STORE" -> "Đăng ký Cửa hàng"
        else -> "Đăng ký Khách hàng"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = titleText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Nếu role đã được chọn trước (Driver/Store), ẩn phần chọn Role đi để tập trung.
            // Nếu là Customer thì có thể để lại (hoặc ẩn luôn cho gọn theo yêu cầu)
            // Theo yêu cầu: "chức năng đăng kí của 2 actor ngoài khách hàng nằm trong chỗ đăng nhập của chúng luôn"
            // Tức là khi vào đây là đã biết role rồi -> Ẩn chọn Role.
            
            /* 
            // Ẩn phần này đi
            Text("Bạn muốn đăng ký làm:", style = MaterialTheme.typography.labelMedium)
            Row(...) { ... }
            */

            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.processIntent(RegisterIntent.NameChanged(it)) },
                label = { Text(if (state.role == "STORE") "Tên Cửa hàng" else "Họ và tên") },
                isError = state.nameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.phone,
                onValueChange = { viewModel.processIntent(RegisterIntent.PhoneChanged(it)) },
                label = { Text("Số điện thoại") },
                isError = state.phoneError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.processIntent(RegisterIntent.EmailChanged(it)) },
                label = { Text("Email") },
                isError = state.emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.pass,
                onValueChange = { viewModel.processIntent(RegisterIntent.PasswordChanged(it)) },
                label = { Text("Mật khẩu") },
                isError = state.passError != null,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.processIntent(RegisterIntent.TogglePasswordVisibility) }) {
                        Icon(if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.confirmPass,
                onValueChange = { viewModel.processIntent(RegisterIntent.ConfirmPasswordChanged(it)) },
                label = { Text("Xác nhận mật khẩu") },
                isError = state.confirmPassError != null,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.processIntent(RegisterIntent.ToggleConfirmPasswordVisibility) }) {
                        Icon(if (state.isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.processIntent(RegisterIntent.SubmitRegister) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White) else Text("ĐĂNG KÝ", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Đã có tài khoản?", color = Color.Gray)
                Text(
                    text = " Đăng nhập ngay",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.processIntent(RegisterIntent.ClickLogin) }
                )
            }
        }
    }
}