package com.example.foodelivery.presentation.auth.forgot_password

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.foodelivery.presentation.auth.forgot_password.contract.ForgotPasswordEffect
import com.example.foodelivery.presentation.auth.forgot_password.contract.ForgotPasswordIntent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Lắng nghe Effect
    LaunchedEffect(true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ForgotPasswordEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ForgotPasswordEffect.Navigation -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quên mật khẩu") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.processIntent(ForgotPasswordIntent.ClickBackToLogin) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Dựa vào step để hiển thị UI
                if (state.step == 1) {
                    StepOneContent(
                        email = state.email,
                        isLoading = state.isLoading,
                        error = state.error,
                        onEmailChange = { viewModel.processIntent(ForgotPasswordIntent.EmailChanged(it)) },
                        onSubmit = { viewModel.processIntent(ForgotPasswordIntent.ClickSendOtp) }
                    )
                } else {
                    StepTwoContent(
                        otp = state.otpCode,
                        newPass = state.newPass,
                        isLoading = state.isLoading,
                        error = state.error,
                        isPassVisible = state.isPasswordVisible,
                        onOtpChange = { viewModel.processIntent(ForgotPasswordIntent.OtpChanged(it)) },
                        onPassChange = { viewModel.processIntent(ForgotPasswordIntent.NewPassChanged(it)) },
                        onTogglePass = { viewModel.processIntent(ForgotPasswordIntent.TogglePasswordVisibility) },
                        onSubmit = { viewModel.processIntent(ForgotPasswordIntent.ClickSubmitReset) }
                    )
                }
            }
        }
    }
}

// --- UI COMPONENTS FOR STEPS ---

@Composable
fun StepOneContent(
    email: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Text(
        text = "Nhập email của bạn",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Chúng tôi sẽ gửi mã xác thực đến email này.",
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
    )

    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email") },
        modifier = Modifier.fillMaxWidth(),
        isError = error != null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
        shape = MaterialTheme.shapes.medium
    )

    if (error != null) {
        Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, start = 8.dp))
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        enabled = !isLoading
    ) {
        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        else Text("GỬI MÃ OTP", fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StepTwoContent(
    otp: String,
    newPass: String,
    isLoading: Boolean,
    error: String?,
    isPassVisible: Boolean,
    onOtpChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onTogglePass: () -> Unit,
    onSubmit: () -> Unit
) {
    Text(
        text = "Đặt lại mật khẩu",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Text(
        text = "Nhập mã OTP và mật khẩu mới.",
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
    )

    // OTP Input
    OutlinedTextField(
        value = otp,
        onValueChange = onOtpChange,
        label = { Text("Mã OTP") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        shape = MaterialTheme.shapes.medium
    )

    Spacer(modifier = Modifier.height(16.dp))

    // New Password Input
    OutlinedTextField(
        value = newPass,
        onValueChange = onPassChange,
        label = { Text("Mật khẩu mới") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onTogglePass) {
                Icon(if (isPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, "Toggle")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        shape = MaterialTheme.shapes.medium
    )

    if (error != null) {
        Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        enabled = !isLoading
    ) {
        if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        else Text("XÁC NHẬN", fontWeight = FontWeight.Bold)
    }
}