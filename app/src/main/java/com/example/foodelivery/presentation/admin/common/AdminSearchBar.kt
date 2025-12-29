package com.example.foodelivery.presentation.admin.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AdminSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Tìm kiếm...",
    isEnabled: Boolean = true,
    onSearchAction: () -> Unit = {} // Hành động khi bấm nút Enter/Search trên bàn phím
) {
    // Quản lý Focus để ẩn bàn phím thủ công
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        enabled = isEnabled,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        // 1. LEADING ICON: Kính lúp (Visual Cue)
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        // 2. TRAILING ICON: Nút Xóa (UX: Chỉ hiện khi có text)
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        // 3. STYLING: Bo góc mềm mại, nền trắng sạch sẽ
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color(0xFFF5F5F5),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,

        // 4. KEYBOARD ACTIONS: Xử lý nút Search trên bàn phím
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearchAction()       // Gọi callback ra ngoài (nếu cần filter ngay lập tức)
                focusManager.clearFocus() // Ẩn bàn phím ngay lập tức -> UX mượt
            }
        )
    )
}

// 5. PREVIEW: Luôn có preview để team member khác dễ hình dung
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun AdminSearchBarPreview() {
    MaterialTheme {
        AdminSearchBar(
            query = "Cơm gà",
            onQueryChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}