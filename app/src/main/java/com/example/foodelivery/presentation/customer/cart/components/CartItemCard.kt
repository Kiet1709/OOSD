package com.example.foodelivery.presentation.customer.cart.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodelivery.presentation.customer.cart.contract.CartItemUiModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CartItemCard(
    item: CartItemUiModel,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Ảnh
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Thông tin + Bộ đếm
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    // Nút Xóa nhỏ
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Xóa",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp).clickable { onRemove() }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(item.price),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bộ đếm (+ 1 -)
                QuantitySelector(qty = item.quantity, onIncrease = onIncrease, onDecrease = onDecrease)
            }
        }
    }
}

@Composable
private fun QuantitySelector(qty: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Nút Trừ
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable { onDecrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
        }

        // Số lượng
        Text(
            text = "$qty",
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold
        )

        // Nút Cộng
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                .clickable { onIncrease() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Color.White)
        }
    }
}