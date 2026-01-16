package com.example.foodelivery.presentation.admin.category.list.contract

import com.example.foodelivery.core.base.ViewState


data class CategoryListState(
    val isLoading: Boolean = false,
    val categories: List<CategoryUiModel> = emptyList(),
    val searchQuery: String = "",
    val categoryToDelete: CategoryUiModel? = null // != null thì hiện Dialog
) : ViewState