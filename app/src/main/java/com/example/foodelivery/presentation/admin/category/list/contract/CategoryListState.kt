package com.example.foodelivery.presentation.admin.category.list.contract

import com.example.foodelivery.core.base.ViewState

data class CategoryUiModel(
    val id: String,
    val name: String,
    val imageUrl: String,
    val totalFoods: Int
)

data class CategoryListState(
    val isLoading: Boolean = false,
    val categories: List<CategoryUiModel> = emptyList(),
    val searchQuery: String = "",
    val categoryToDelete: CategoryUiModel? = null // != null thì hiện Dialog
) : ViewState