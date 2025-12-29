package com.example.foodelivery.presentation.admin.order.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.presentation.admin.order.list.contract.OrderUiModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun OrderItemCard(
    item: OrderUiModel,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("#${item.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(item.createdAt, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = Color.LightGray)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(item.customerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${item.itemsCount} món: ${item.itemsSummary}", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray, maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                val price = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(item.totalAmount)
                Text(price, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                if (item.status == OrderStatus.NEW) {
                    Row {
                        IconButton(onClick = onCancel) { Icon(Icons.Outlined.Cancel, null, tint = MaterialTheme.colorScheme.error) }
                        IconButton(onClick = onAccept) { Icon(Icons.Outlined.CheckCircle, null, tint = Color(0xFF4CAF50)) }
                    }
                } else {
                    // Badge trạng thái đơn giản
                    Surface(color = Color(0xFFEEEEEE), shape = RoundedCornerShape(4.dp)) {
                        Text(item.status.title, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}