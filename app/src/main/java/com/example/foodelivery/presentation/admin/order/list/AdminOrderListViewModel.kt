package com.example.foodelivery.presentation.admin.order.list

import androidx.lifecycle.viewModelScope
import com.example.foodelivery.core.base.BaseViewModel
import com.example.foodelivery.core.common.Resource
import com.example.foodelivery.domain.model.Order
import com.example.foodelivery.domain.model.OrderStatus
import com.example.foodelivery.domain.usecase.order.GetAllOrdersUseCase
import com.example.foodelivery.domain.usecase.order.UpdateOrderStatusUseCase
import com.example.foodelivery.presentation.admin.order.list.contract.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AdminOrderListViewModel @Inject constructor(
    private val getAllOrdersUseCase: GetAllOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
) : BaseViewModel<OrderListState, OrderListIntent, OrderListEffect>(
    initialState = OrderListState()
) {

    private var searchJob: Job? = null

    init {
        loadOrders()
    }

    fun setEvent(intent: OrderListIntent) {
        handleIntent(intent)
    }

    override fun handleIntent(intent: OrderListIntent) {
        when (intent) {
            is OrderListIntent.ChangeTab -> {
                setState { copy(selectedTab = intent.status) }
                refreshDisplayList()
            }

            is OrderListIntent.SearchOrder -> {
                setState { copy(searchQuery = intent.query) }
                performDebounceSearch()
            }

            OrderListIntent.OpenFilterSheet -> {
                setState { copy(isFilterSheetVisible = true) }
            }

            OrderListIntent.CloseFilterSheet -> {
                setState { copy(isFilterSheetVisible = false) }
            }

            is OrderListIntent.ApplyFilterAndSort -> {
                setState {
                    copy(
                        filterCriteria = intent.criteria,
                        sortOption = intent.sortOption,
                        isFilterSheetVisible = false
                    )
                }
                refreshDisplayList()
            }

            is OrderListIntent.ClickOrder -> {
                setEffect { OrderListEffect.NavigateToDetail(intent.id) }
            }

            is OrderListIntent.QuickUpdateStatus -> {
                updateStatus(intent.id, intent.newStatus)
            }

            is OrderListIntent.QuickAcceptOrder -> {
                updateStatus(intent.id, OrderStatus.CONFIRMED)
            }

            is OrderListIntent.QuickCancelOrder -> {
                updateStatus(intent.id, OrderStatus.CANCELLED)
            }
        }
    }

    /**
     * Load orders từ Firebase (Real-time)
     */
    private fun loadOrders() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            // getAllOrdersUseCase trả về Flow - tự động update khi Firebase thay đổi
            getAllOrdersUseCase().collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        val orders = result.data ?: emptyList()
                        val uiModels = orders.map { it.toUiModel() }

                        setState {
                            copy(
                                isLoading = false,
                                allOrders = uiModels
                            )
                        }
                        refreshDisplayList()
                    }

                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect {
                            OrderListEffect.ShowToast(result.message ?: "Lỗi tải dữ liệu")
                        }
                    }

                    else -> {}
                }
            }
        }
    }
    /**
     * Debounce search - chờ 300ms rồi search
     */
    private fun performDebounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            refreshDisplayList()
        }
    }
    /**
     * Refresh danh sách hiển thị dựa trên Tab, Search, Filter, Sort
     */
    private fun refreshDisplayList() {
        val state = currentState

        var filtered = state.allOrders

        // 1. Filter by Tab (Status)
        filtered = filtered.filter { order ->
            order.status == state.selectedTab
        }

        // 2. Filter by Search Query
        filtered = filtered.filter { order ->
            state.searchQuery.isBlank() ||
                    order.id.contains(state.searchQuery, ignoreCase = true)
        }

        // 3. Filter by Price Range
        filtered = filtered.filter { order ->
            val min = state.filterCriteria.minPrice.toDoubleOrNull() ?: 0.0
            val max = state.filterCriteria.maxPrice.toDoubleOrNull() ?: Double.MAX_VALUE
            order.totalAmount in min..max
        }

        // 4. Sort
        val sorted = filtered.sortedWith { o1, o2 ->
            when (state.sortOption) {
                SortOption.NEWEST -> o2.createdAt.compareTo(o1.createdAt)
                SortOption.OLDEST -> o1.createdAt.compareTo(o2.createdAt)
                SortOption.PRICE_HIGH -> o2.totalAmount.compareTo(o1.totalAmount)
                SortOption.PRICE_LOW -> o1.totalAmount.compareTo(o2.totalAmount)
            }
        }

        setState { copy(displayedOrders = sorted) }
    }

    /**
     * Update order status
     */
    private fun updateStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            try {
                // Optimistic update UI
                val updatedList = currentState.displayedOrders.map { order ->
                    if (order.id == orderId) {
                        order.copy(status = newStatus)
                    } else {
                        order
                    }
                }
                setState { copy(displayedOrders = updatedList) }

                // Call UseCase - truyền status.value (String)
                val result = updateOrderStatusUseCase(
                    orderId = orderId,
                    status = newStatus.value,
                    driverId = null
                )

                if (result is Resource.Success) {
                    setEffect { OrderListEffect.ShowToast("Cập nhật thành công") }
                } else {
                    setEffect {
                        OrderListEffect.ShowToast(
                            (result as? Resource.Error)?.message ?: "Lỗi cập nhật"
                        )
                    }
                    // Reload nếu lỗi
                    loadOrders()
                }

            } catch (e: Exception) {
                setEffect { OrderListEffect.ShowToast("Lỗi: ${e.message}") }
                loadOrders()
            }
        }
    }
}

// ============================================
// MAPPER: Order (Domain) → OrderUiModel
// ============================================

private fun Order.toUiModel(): OrderUiModel {
    // Format timestamp thành string
    val dateFormatter = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
    val dateString = if (timestamp > 0) {
        dateFormatter.format(Date(timestamp))
    } else {
        "N/A"
    }

    // Tóm tắt items
    val itemsSummary = items.joinToString(separator = ", ") {
        "${it.quantity}x ${it.name}"
    }
    val totalCount = items.sumOf { it.quantity }

    // Tạo customer name từ userId
    val customerName = "Khách ${userId.takeLast(4)}"

    return OrderUiModel(
        id = id,
        customerName = customerName,
        totalAmount = totalPrice,
        itemsSummary = itemsSummary,
        status = status,
        createdAt = dateString,
        itemsCount = totalCount
    )
}