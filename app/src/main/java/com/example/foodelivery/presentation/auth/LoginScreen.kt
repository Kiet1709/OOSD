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
import com.example.foodelivery.presentation.auth.contract.LoginEffect
import com.example.foodelivery.presentation.auth.contract.LoginIntent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    onNavigation: (LoginEffect.Navigation) -> Unit, // Callback điều hướng ra ngoài
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Lắng nghe Effect (Toast, Navigation)
    LaunchedEffect(true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is LoginEffect.Navigation -> {
                    onNavigation(effect)
                }
            }
        }
    }

    // UI Content
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Text(
                text = "Chào mừng trở lại!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Đăng nhập để tiếp tục món ngon",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Email Input
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.processIntent(LoginIntent.EmailChanged(it)) },
                label = { Text("Email") },
                isError = state.emailError != null,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            if (state.emailError != null) {
                Text(
                    text = state.emailError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
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
                        Icon(
                            imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
            if (state.passwordError != null) {
                Text(
                    text = state.passwordError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
                )
            }

            // Forgot Password Link
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { viewModel.processIntent(LoginIntent.ClickForgotPassword) }) {
                    Text("Quên mật khẩu?")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = { viewModel.processIntent(LoginIntent.SubmitLogin) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ĐĂNG NHẬP", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skip Login
            OutlinedButton(
                onClick = { viewModel.processIntent(LoginIntent.ClickSkipLogin) },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Dùng thử không cần tài khoản")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer Register
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chưa có tài khoản?", color = Color.Gray)
                Text(
                    text = " Đăng ký ngay",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { viewModel.processIntent(LoginIntent.ClickRegister) }
                )
            }
        }
    }
}