package com.example.foodelivery.presentation.admin.order.list.contract

import com.example.foodelivery.core.base.ViewIntent
import com.example.foodelivery.domain.model.OrderStatus

sealed class OrderListIntent : ViewIntent {
    // 1. Tab & Search
    data class ChangeTab(val status: OrderStatus) : OrderListIntent()
    data class SearchOrder(val query: String) : OrderListIntent()

    // 2. Filter Sheet
    object OpenFilterSheet : OrderListIntent()
    object CloseFilterSheet : OrderListIntent()
    data class ApplyFilterAndSort(val criteria: FilterCriteria, val sortOption: SortOption) : OrderListIntent()

    // 3. Actions (List Only)
    data class ClickOrder(val id: String) : OrderListIntent()
    data class QuickAcceptOrder(val id: String) : OrderListIntent() // Chuyển sang PREPARING
    data class QuickCancelOrder(val id: String) : OrderListIntent() // Chuyển sang CANCELLED
    data class QuickUpdateStatus(val id: String, val newStatus: OrderStatus) : OrderListIntent()}