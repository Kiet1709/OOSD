package com.example.foodelivery.presentation.customer.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
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
import com.example.foodelivery.core.common.toVndCurrency
import com.example.foodelivery.presentation.customer.home.contract.FoodUiModel
import com.example.foodelivery.ui.theme.PrimaryColor

@Composable
fun HomeFoodSection(
    title: String,
    foods: List<FoodUiModel>,
    onFoodClick: (String) -> Unit,
    modifier: Modifier = Modifier // [QUAN TRỌNG]: Phải có tham số này
) {
    Column(modifier = modifier) {
        // Title Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Xem tất cả",
                color = PrimaryColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { /* Handle View All */ }
            )
        }

        // Horizontal List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(foods) { food ->
                FoodCard(food = food, onClick = { onFoodClick(food.id) })
            }
        }
    }
}

@Composable
fun FoodCard(food: FoodUiModel, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column {
            // Image Box
            Box(modifier = Modifier.height(110.dp)) {
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                // Rating Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${food.rating}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            // Info
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = food.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = food.price.toVndCurrency(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    )
                }
            }
        }
    }
}