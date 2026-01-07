package com.example.foodelivery.presentation.auth.login

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
import com.example.foodelivery.presentation.auth.contract.LoginEffect
import com.example.foodelivery.presentation.auth.contract.LoginIntent

@Composable
fun LoginScreen(
    onNavigation: (LoginEffect.Navigation) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is LoginEffect.Navigation -> onNavigation(effect)
            }
        }
    }

    // Xác định tiêu đề dựa trên Role đang chọn
    val titleText = when(state.selectedRole) {
        "DRIVER" -> "Đăng nhập Tài xế"
        "STORE" -> "Đăng nhập Chủ quán"
        else -> "Food Delivery" // Customer
    }
    
    val subtitleText = when(state.selectedRole) {
        "DRIVER" -> "Chào mừng bạn gia nhập đội ngũ giao hàng"
        "STORE" -> "Quản lý cửa hàng của bạn"
        else -> "Đăng nhập để tiếp tục món ngon"
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
            Text(
                text = subtitleText,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.processIntent(LoginIntent.EmailChanged(it)) },
                label = { Text("Email") },
                isError = state.emailError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )
            if (state.emailError != null) {
                Text(text = state.emailError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.pass,
                onValueChange = { viewModel.processIntent(LoginIntent.PasswordChanged(it)) },
                label = { Text("Mật khẩu") },
                isError = state.passwordError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.processIntent(LoginIntent.TogglePasswordVisibility) }) {
                        Icon(if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle Password")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
            )
            if (state.passwordError != null) {
                Text(text = state.passwordError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { viewModel.processIntent(LoginIntent.ClickForgotPassword) }) {
                    Text("Quên mật khẩu?")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.processIntent(LoginIntent.SubmitLogin) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator(color = Color.White) else Text("ĐĂNG NHẬP", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chưa có tài khoản?", color = Color.Gray)
                Text(
                    text = " Đăng ký ngay",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.processIntent(LoginIntent.ClickRegister) }
                )
            }
            
            // SECTION CHUYỂN ĐỔI CHẾ ĐỘ
            Spacer(modifier = Modifier.height(48.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            // Logic hiển thị nút chuyển đổi:
            // Nếu đang là Customer -> Hiện nút Driver & Store
            // Nếu đang là Driver/Store -> Hiện nút "Quay lại Khách hàng"
            
            if (state.selectedRole == "CUSTOMER") {
                Text("Dành cho Đối tác", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { viewModel.switchRole("DRIVER") }) {
                        Text("Tài xế")
                    }
                    TextButton(onClick = { viewModel.switchRole("STORE") }) {
                        Text("Chủ cửa hàng")
                    }
                }
            } else {
                TextButton(onClick = { viewModel.switchRole("CUSTOMER") }) {
                    Text("← Quay lại Đăng nhập Khách hàng")
                }
            }
        }
    }
}