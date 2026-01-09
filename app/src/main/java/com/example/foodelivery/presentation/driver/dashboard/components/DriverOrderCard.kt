package com.example.foodelivery.presentation.driver.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.presentation.driver.dashboard.contract.DriverOrderUiModel
import com.example.foodelivery.ui.theme.PrimaryColor

@Composable
fun DriverOrderCard(
    order: DriverOrderUiModel,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Info
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("${order.distanceKm} km", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.NearMe, null, modifier = Modifier.size(14.dp)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = PrimaryColor.copy(0.1f), labelColor = PrimaryColor, iconContentColor = PrimaryColor),
                    border = null, modifier = Modifier.height(28.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(order.timeAgo, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(12.dp))

            // Timeline Route
            Row {
                Column(Modifier.width(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Circle, null, tint = Color.Gray, modifier = Modifier.size(10.dp))
                    Canvas(Modifier.width(2.dp).height(38.dp).padding(vertical = 4.dp)) {
                        drawLine(Color.LightGray, Offset(0f, 0f), Offset(0f, size.height), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)), strokeWidth = 4f)
                    }
                    Icon(Icons.Default.LocationOn, null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(order.restaurantName, fontWeight = FontWeight.Bold)
                    Text("Điểm lấy hàng", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(16.dp))
                    Text("Khách hàng", fontWeight = FontWeight.Bold)
                    Text(order.customerAddress, color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(Modifier.height(12.dp))

            // Footer Actions
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Thu nhập", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(order.earning.toVndCurrency(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = PrimaryColor)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onReject, shape = RoundedCornerShape(8.dp), modifier = Modifier.height(40.dp)) { Text("Bỏ qua", color = Color.Gray) }
                    Button(onClick = onAccept, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor), modifier = Modifier.height(40.dp)) { Text("Nhận đơn") }
                }
            }
        }
    }
}