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
    initialState = OrderListState() // FIX: Truyền State vào constructor
) {

    private var searchJob: Job? = null

    init {
        loadOrders()
    }

    // Hàm public để UI gọi (Thay thế processIntent cũ)
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
            OrderListIntent.OpenFilterSheet -> setState { copy(isFilterSheetVisible = true) }
            OrderListIntent.CloseFilterSheet -> setState { copy(isFilterSheetVisible = false) }
            is OrderListIntent.ApplyFilterAndSort -> {
                setState {
                    copy(filterCriteria = intent.criteria, sortOption = intent.sortOption, isFilterSheetVisible = false)
                }
                refreshDisplayList()
            }
            // Actions
            is OrderListIntent.ClickOrder -> setEffect { OrderListEffect.NavigateToDetail(intent.id) }
            is OrderListIntent.QuickUpdateStatus -> updateStatus(intent.id, intent.newStatus)
            is OrderListIntent.QuickAcceptOrder -> updateStatus(intent.id, OrderStatus.PREPARING)
            is OrderListIntent.QuickCancelOrder -> updateStatus(intent.id, OrderStatus.CANCELLED)
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            // Sử dụng Flow Realtime
            getAllOrdersUseCase().collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        val uiModels = result.data?.map { it.toUiModel() } ?: emptyList()
                        setState { copy(isLoading = false, allOrders = uiModels) }
                        refreshDisplayList()
                    }
                    is Resource.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect { OrderListEffect.ShowToast(result.message ?: "Lỗi tải dữ liệu") }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun performDebounceSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            refreshDisplayList()
        }
    }

    private fun refreshDisplayList() {
        val s = currentState
        val displayed = s.allOrders.filter { order ->
            if (s.selectedTab == OrderStatus.HISTORY) {
                // Tab lịch sử gồm cả Đã giao, Đã hủy, Đang giao (tùy nghiệp vụ)
                order.status == OrderStatus.HISTORY || order.status == OrderStatus.DELIVERING || order.status == OrderStatus.CANCELLED
            } else {
                order.status == s.selectedTab
            }
        }.filter { order ->
            s.searchQuery.isBlank() || order.id.contains(s.searchQuery, ignoreCase = true)
        }.filter { order ->
            val min = s.filterCriteria.minPrice.toDoubleOrNull() ?: 0.0
            val max = s.filterCriteria.maxPrice.toDoubleOrNull() ?: Double.MAX_VALUE
            order.totalAmount in min..max
        }.sortedWith { o1, o2 ->
            when (s.sortOption) {
                SortOption.NEWEST -> o2.id.compareTo(o1.id) // Sort tạm theo ID
                SortOption.OLDEST -> o1.id.compareTo(o2.id)
                SortOption.PRICE_HIGH -> o2.totalAmount.compareTo(o1.totalAmount)
                SortOption.PRICE_LOW -> o1.totalAmount.compareTo(o2.totalAmount)
            }
        }
        setState { copy(displayedOrders = displayed) }
    }

    private fun updateStatus(id: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            // Optimistic Update
            val currentList = currentState.displayedOrders.map {
                if (it.id == id) it.copy(status = newStatus) else it
            }
            setState { copy(displayedOrders = currentList) }

            // FIX: Truyền .name (String) vào UseCase
            val result = updateOrderStatusUseCase(id, newStatus.name)

            if (result is Resource.Success) {
                setEffect { OrderListEffect.ShowToast("Cập nhật thành công") }
            } else {
                setEffect { OrderListEffect.ShowToast("Lỗi: ${result.message}") }
                loadOrders() // Revert lại dữ liệu cũ
            }
        }
    }
}

// FIX MAPPER: Dùng timestamp và format date
private fun Order.toUiModel(): OrderUiModel {
    val date = if (this.timestamp > 0) Date(this.timestamp) else Date()
    val formatter = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
    val dateString = formatter.format(date)

    // itemsSummary: Hiển thị tóm tắt món ăn
    val itemsSummary = this.items.joinToString { "${it.quantity}x ${it.name}" }
    val totalCount = this.items.sumOf { it.quantity }

    return OrderUiModel(
        id = this.id,
        customerName = "Khách ${this.userId.takeLast(4)}",
        totalAmount = this.totalPrice,
        itemsSummary = itemsSummary,
        status = this.status, // Giờ đây Domain và UI dùng chung Enum OrderStatus
        createdAt = dateString,
        itemsCount = totalCount
    )
}