package com.example.foodelivery.presentation.customer.home.contract

import com.example.foodelivery.core.base.ViewState

// --- UI MODELS (Dữ liệu hiển thị) ---
data class CategoryUiModel(
    val id: String,
    val name: String,
    val iconUrl: String
)

data class FoodUiModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val rating: Double,
    val time: String // VD: "15-20 min"
)

// --- STATE ---
data class CustomerHomeState(
    val isLoading: Boolean = false,
    val userName: String = "Bạn", // Tên người dùng hiển thị ở Header
    val avatarUrl: String? = null,

    // Dữ liệu danh sách
    val categories: List<CategoryUiModel> = emptyList(),
    val popularFoods: List<FoodUiModel> = emptyList(),
    val recommendedFoods: List<FoodUiModel> = emptyList(),

    val errorMessage: String? = null
) : ViewState