package com.example.foodelivery.presentation.admin.order.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.foodelivery.presentation.admin.order.list.contract.FilterCriteria
import com.example.foodelivery.presentation.admin.order.list.contract.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFilterBottomSheet(
    currentSort: SortOption,
    currentFilter: FilterCriteria,
    onDismiss: () -> Unit,
    onApply: (FilterCriteria, SortOption) -> Unit
) {
    var tempSort by remember { mutableStateOf(currentSort) }
    var minPrice by remember { mutableStateOf(currentFilter.minPrice) }
    var maxPrice by remember { mutableStateOf(currentFilter.maxPrice) }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp).verticalScroll(rememberScrollState())) {
            Text("Bộ lọc & Sắp xếp", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Sắp xếp", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            SortOption.values().forEach { option ->
                Row(modifier = Modifier.fillMaxWidth().height(40.dp).selectable(selected = (tempSort == option), onClick = { tempSort = option }), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = (tempSort == option), onClick = { tempSort = option })
                    Text(option.title, modifier = Modifier.padding(start = 8.dp))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Khoảng giá (VNĐ)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = minPrice, onValueChange = { if (it.all { c -> c.isDigit() }) minPrice = it }, label = { Text("Thấp nhất") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
                OutlinedTextField(value = maxPrice, onValueChange = { if (it.all { c -> c.isDigit() }) maxPrice = it }, label = { Text("Cao nhất") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { onApply(FilterCriteria(minPrice, maxPrice), tempSort) }, modifier = Modifier.fillMaxWidth()) { Text("Áp dụng") }
        }
    }
}