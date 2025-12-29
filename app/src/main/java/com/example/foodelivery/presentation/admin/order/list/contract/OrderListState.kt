package com.example.foodelivery.presentation.admin.order.list.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.OrderStatus

// --- ENUMS & MODELS ---


enum class SortOption(val title: String) {
    NEWEST("Mới nhất"),
    OLDEST("Cũ nhất"),
    PRICE_HIGH("Giá cao → Thấp"),
    PRICE_LOW("Giá thấp → Cao")
}

data class FilterCriteria(
    val minPrice: String = "",
    val maxPrice: String = ""
)

// UI Model hiển thị
data class OrderUiModel(
    val id: String,
    val customerName: String, // Có thể map từ userId
    val totalAmount: Double,
    val itemsSummary: String,
    val status: OrderStatus,
    val createdAt: String,
    val itemsCount: Int
)

// --- MVI CONTRACT ---

data class OrderListState(
    val isLoading: Boolean = false,
    val allOrders: List<OrderUiModel> = emptyList(),       // Dữ liệu gốc từ Firestore
    val displayedOrders: List<OrderUiModel> = emptyList(), // Dữ liệu sau khi Filter/Sort

    // Filter States
    val selectedTab: OrderStatus = OrderStatus.NEW,
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.NEWEST,
    val filterCriteria: FilterCriteria = FilterCriteria(),
    val isFilterSheetVisible: Boolean = false
) : ViewState