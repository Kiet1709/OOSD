package com.example.foodelivery.presentation.customer.cart.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.presentation.customer.cart.contract.CartItemUiModel

@Composable
fun CartItemCard(
    item: CartItemUiModel,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    isInteractive: Boolean = true
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.price.toVndCurrency(), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                if (isInteractive) {
                    QuantitySelector(item.quantity, onIncrease, onDecrease)
                } else {
                    Text("Số lượng: ${item.quantity}", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                }
            }
            // This IconButton will now only be shown if isInteractive is true
            if (isInteractive) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Remove")
                }
            }
        }
    }
}

@Composable
private fun QuantitySelector(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedIconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease")
        }
        Text(
            text = quantity.toString(),
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Add, contentDescription = "Increase")
        }
    }
}