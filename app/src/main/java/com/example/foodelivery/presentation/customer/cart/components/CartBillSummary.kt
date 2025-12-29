package com.example.foodelivery.presentation.customer.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartBillSummary(
    subTotal: Double,
    deliveryFee: Double,
    discount: Double,
    finalTotal: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Chi tiết thanh toán", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        BillRow("Tổng tiền hàng", subTotal)
        BillRow("Phí vận chuyển", deliveryFee)
        if (discount > 0) {
            BillRow("Giảm giá", -discount, isDiscount = true)
        }

        Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tổng thanh toán", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(
                text = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(finalTotal),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun BillRow(label: String, amount: Double, isDiscount: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount),
            color = if (isDiscount) Color(0xFF4CAF50) else Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}