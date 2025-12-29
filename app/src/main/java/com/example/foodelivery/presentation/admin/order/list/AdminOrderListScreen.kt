package com.example.foodelivery.presentation.admin.order.list

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
// --- QUAN TRỌNG: Import OrderStatus từ Domain ---
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.presentation.admin.common.AdminSearchBar
import com.example.foodelivery.presentation.admin.order.list.components.OrderFilterBottomSheet
import com.example.foodelivery.presentation.admin.order.list.components.OrderItemCard
import com.example.foodelivery.presentation.admin.order.list.components.OrderStatusTabs
import com.example.foodelivery.presentation.admin.order.list.contract.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderListScreen(
    navController: NavController,
    viewModel: AdminOrderListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is OrderListEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is OrderListEffect.NavigateToDetail -> {
                    // TODO: Điều hướng chi tiết
                }
            }
        }
    }

    if (state.isFilterSheetVisible) {
        OrderFilterBottomSheet(
            currentSort = state.sortOption,
            currentFilter = state.filterCriteria,
            onDismiss = { viewModel.setEvent(OrderListIntent.CloseFilterSheet) },
            onApply = { criteria, sort -> viewModel.setEvent(OrderListIntent.ApplyFilterAndSort(criteria, sort)) }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Quản lý Đơn Hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Component Tabs này cũng cần import OrderStatus (nếu nó nằm file riêng)
            OrderStatusTabs(
                selectedTab = state.selectedTab,
                onTabSelected = { viewModel.setEvent(OrderListIntent.ChangeTab(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                AdminSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.setEvent(OrderListIntent.SearchOrder(it)) },
                    placeholder = "Tìm mã đơn...",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilledTonalIconButton(
                    onClick = { viewModel.setEvent(OrderListIntent.OpenFilterSheet) },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Lọc")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (state.displayedOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy đơn hàng nào", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.displayedOrders) { order ->
                        OrderItemCard(
                            item = order,
                            onClick = { viewModel.setEvent(OrderListIntent.ClickOrder(order.id)) },
                            onAccept = { viewModel.setEvent(OrderListIntent.QuickAcceptOrder(order.id)) },
                            onCancel = { viewModel.setEvent(OrderListIntent.QuickCancelOrder(order.id)) }
                        )
                    }
                }
            }
        }
    }
}