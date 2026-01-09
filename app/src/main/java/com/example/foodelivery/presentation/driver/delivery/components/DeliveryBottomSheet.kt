package com.example.foodelivery.presentation.driver.delivery.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodelivery.presentation.driver.delivery.contract.DeliveryOrderInfo
import com.example.foodelivery.presentation.driver.delivery.contract.DeliveryStep
import com.example.foodelivery.ui.theme.PrimaryColor
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DeliveryBottomSheet(
    order: DeliveryOrderInfo,
    currentStep: DeliveryStep,
    onMainAction: () -> Unit,
    onCall: () -> Unit,
    onChat: () -> Unit,
    onMapClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            // 1. Header: Trạng thái hiện tại
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Navigation, contentDescription = null, tint = PrimaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentStep.instruction,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Thông tin chính (Logic hiển thị tùy theo bước)
            // Nếu đang đi lấy món -> Hiện tên Quán. Nếu đang đi giao -> Hiện tên Khách.
            val isPickupPhase = currentStep == DeliveryStep.HEADING_TO_RESTAURANT || currentStep == DeliveryStep.PICKING_UP

            val displayTitle = if (isPickupPhase) order.restaurantName else order.customerName
            val displayAddress = if (isPickupPhase) order.restaurantAddress else order.customerAddress
            val displayIcon = if (isPickupPhase) Icons.Default.Store else Icons.Default.Person

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar / Icon
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF5F5F5),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(displayIcon, contentDescription = null, tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = displayAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }

                // Action Buttons (Gọi / Map)
                IconButton(
                    onClick = onCall,
                    modifier = Modifier.background(Color(0xFFE0F7FA), CircleShape)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = PrimaryColor)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onMapClick,
                    modifier = Modifier.background(Color(0xFFE0F7FA), CircleShape)
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Map", tint = PrimaryColor)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Hiển thị tiền thu hộ (COD) nếu có
            if (order.totalAmount > 0) {
                val formattedPrice = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(order.totalAmount)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thu tiền mặt:", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    Text(formattedPrice, fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Nút Hành động chính (Nút to nhất)
            Button(
                onClick = onMainAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text(
                    text = currentStep.buttonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}