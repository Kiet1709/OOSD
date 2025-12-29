package com.example.foodelivery.presentation.driver.dashboard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Header: Giá tiền & Khoảng cách
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Earning Badge
                Surface(
                    color = PrimaryColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.earning.toVndCurrency(),
                        color = PrimaryColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Distance & Time
                Column(horizontalAlignment = Alignment.End) {
                    Text("${order.distanceKm} km", fontWeight = FontWeight.Bold)
                    Text(order.timeAgo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Route Info (Pick up -> Drop off)
            // Pick Up (Nhà hàng)
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Store, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(order.restaurantName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(order.restaurantAddress, color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }

            // Dotted Line giả lập
            Box(
                modifier = Modifier
                    .padding(start = 9.dp, top = 4.dp, bottom = 4.dp)
                    .height(20.dp)
                    .width(2.dp)
                    .background(Color.LightGray)
            )

            // Drop Off (Khách)
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Giao đến khách hàng", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(order.customerAddress, color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Action Buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Reject Button
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.Gray)
                ) {
                    Text("Bỏ qua", color = Color.Gray, fontWeight = FontWeight.Bold)
                }

                // Accept Button
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Nhận đơn", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}