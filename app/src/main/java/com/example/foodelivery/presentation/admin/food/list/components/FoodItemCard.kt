package com.example.foodelivery.presentation.admin.food.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
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
import com.example.foodelivery.presentation.admin.food.list.contract.FoodUiModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FoodItemCard(
    item: FoodUiModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onEditClick() }, // Click vào card -> Sửa
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Ảnh món ăn
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Thông tin chính
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Format tiền VND chuẩn
                val priceFormatted = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(item.price)
                Text(
                    text = priceFormatted,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Chip trạng thái (Click để đổi)
                StatusChip(isAvailable = item.isAvailable, onClick = onToggleStatus)
            }

            // 3. Các nút hành động
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Sửa", tint = Color.Gray)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(isAvailable: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE) // Xanh nhạt / Đỏ nhạt
    ) {
        Text(
            text = if (isAvailable) "Đang bán" else "Hết hàng",
            style = MaterialTheme.typography.labelSmall,
            color = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFC62828), // Xanh đậm / Đỏ đậm
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontWeight = FontWeight.Medium
        )
    }
}