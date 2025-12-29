package com.example.foodelivery.presentation.admin.food.list.contract
import com.example.foodelivery.core.base.ViewState
// Model hiển thị UI (chỉ chứa data cần thiết để vẽ)
data class FoodUiModel(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val isAvailable: Boolean = true // Trạng thái còn hàng

)

// Trạng thái toàn màn hình
data class FoodListState(
    val isLoading: Boolean = false,
    val foodList: List<FoodUiModel> = emptyList(),      // Dữ liệu gốc
    val displayedList: List<FoodUiModel> = emptyList(), // Dữ liệu đang hiển thị (sau khi search/filter)
    val searchQuery: String = ""
) : ViewState