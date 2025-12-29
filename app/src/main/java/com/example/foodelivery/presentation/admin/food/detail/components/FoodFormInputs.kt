package com.example.foodelivery.presentation.admin.food.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FoodFormInputs(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,

    price: String,
    onPriceChange: (String) -> Unit,
    priceError: String?,

    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Column {
        // 1. Nhập Tên
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Tên món ăn") },
            modifier = Modifier.fillMaxWidth(),
            isError = nameError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        if (nameError != null) {
            Text(
                text = nameError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Nhập Giá
        OutlinedTextField(
            value = price,
            onValueChange = { input ->
                // Chỉ cho phép nhập số
                if (input.all { it.isDigit() }) onPriceChange(input)
            },
            label = { Text("Giá bán (VNĐ)") },
            modifier = Modifier.fillMaxWidth(),
            isError = priceError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )
        if (priceError != null) {
            Text(
                text = priceError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Nhập Mô tả
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Mô tả chi tiết") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
    }
}