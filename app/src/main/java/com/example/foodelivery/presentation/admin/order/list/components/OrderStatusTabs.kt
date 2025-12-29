package com.example.foodelivery.presentation.admin.order.list.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
// [QUAN TRỌNG] Thêm dòng này:
import com.example.foodelivery.domain.model.OrderStatus

@Composable
fun OrderStatusTabs(
    selectedTab: OrderStatus, // Phải là OrderStatus của Domain
    onTabSelected: (OrderStatus) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 0.dp // Để tab sát lề
    ) {
        // Duyệt qua tất cả các giá trị của Enum Domain
        OrderStatus.values().forEach { status ->
            Tab(
                selected = selectedTab == status,
                onClick = { onTabSelected(status) },
                text = { Text(text = status.title) } // Enum Domain đã có trường title
            )
        }
    }
}