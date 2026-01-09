package com.example.foodelivery.presentation.customer.home.contract

import com.example.foodelivery.core.base.ViewState
import com.example.foodelivery.domain.model.Food
import com.example.foodelivery.domain.model.User

// --- UI MODELS (Dữ liệu hiển thị) ---
data class CategoryUiModel(
    val id: String,
    val name: String,
    val iconUrl: String
)


// --- STATE ---
data class CustomerHomeState(
    val isLoading: Boolean = false,
//    val userName: String = "Bạn", // Tên người dùng hiển thị ở Header
    val user: User? = null, // [SỬA]: Dùng User object thay vì String rời rạc
    val avatarUrl: String? = null,

    // Dữ liệu danh sách
    val categories: List<CategoryUiModel> = emptyList(),
    val popularFoods: List<Food> = emptyList(),
    val recommendedFoods: List<Food> = emptyList(),
    val errorMessage: String? = null
) : ViewState