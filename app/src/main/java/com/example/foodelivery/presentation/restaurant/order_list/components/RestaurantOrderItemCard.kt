package com.example.foodelivery.presentation.restaurant.order_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.model.toColor
import com.example.foodelivery.domain.model.toVietnamese
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RestaurantOrderItemCard(
    order: Order,
    onUpdateStatus: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Đơn hàng #${order.id.take(6).uppercase()}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(order.status.toVietnamese(), color = order.status.toColor(), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Ngày đặt: ${formatTimestamp(order.timestamp)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text("Địa chỉ: ${order.shippingAddress}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            // Action buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                when (order.status) {
                    OrderStatus.PENDING -> {
                        Button(onClick = { onUpdateStatus(order.id, OrderStatus.CONFIRMED.name) }) {
                            Text("Xác nhận")
                        }
                    }
                    OrderStatus.CONFIRMED -> {
                        Button(onClick = { onUpdateStatus(order.id, OrderStatus.PREPARING.name) }) {
                            Text("Bắt đầu chuẩn bị")
                        }
                    }
                    OrderStatus.PREPARING -> {
                        Button(onClick = { onUpdateStatus(order.id, OrderStatus.DELIVERING.name) }) {
                            Text("Sẵn sàng giao")
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}