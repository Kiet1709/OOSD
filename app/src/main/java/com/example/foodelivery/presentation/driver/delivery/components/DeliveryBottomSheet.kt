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
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.presentation.driver.delivery.contract.DeliveryOrderInfo
import com.example.foodelivery.presentation.driver.delivery.contract.DeliveryStep
import com.example.foodelivery.ui.theme.PrimaryColor

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
            // 1. Chỉ dẫn (Instruction)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Navigation, null, tint = PrimaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = currentStep.instruction, // VD: "Di chuyển đến nhà hàng"
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Thông tin đối tác (Quán hoặc Khách tùy giai đoạn)
            val isPickupPhase = currentStep == DeliveryStep.HEADING_TO_RESTAURANT || currentStep == DeliveryStep.PICKING_UP
            val name = if (isPickupPhase) order.restaurantName else order.customerName
            val address = if (isPickupPhase) order.restaurantAddress else order.customerAddress
            val icon = if (isPickupPhase) Icons.Default.Store else Icons.Default.Person

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape, color = Color(0xFFF5F5F5),
                    modifier = Modifier.size(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(address, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, maxLines = 2)
                }

                // Các nút phụ: Gọi, Map
                IconButton(onClick = onCall, modifier = Modifier.background(Color(0xFFE0F7FA), CircleShape)) {
                    Icon(Icons.Default.Call, null, tint = PrimaryColor)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onMapClick, modifier = Modifier.background(Color(0xFFE0F7FA), CircleShape)) {
                    Icon(Icons.Default.Map, null, tint = PrimaryColor)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Thu tiền (COD)
            if (order.totalAmount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)).padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Thu tiền mặt:", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    Text(order.totalAmount.toVndCurrency(), fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. NÚT HÀNH ĐỘNG CHÍNH (MAIN ACTION)
            Button(
                onClick = onMainAction,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text(
                    text = currentStep.buttonText, // VD: "ĐÃ ĐẾN QUÁN"
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}